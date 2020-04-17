package coupledsims.server;

import assignments.util.mainArgs.ServerArgsProcessor;
import coupledsims.client.RemoteClient;
import inputport.ConnectionListener;
import inputport.InputPort;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import port.ATracingConnectionListener;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.Tracer;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalMade;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.gipc.GIPCObjectLookedUp;
import util.trace.port.rpc.gipc.GIPCObjectRegistered;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryCreated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.util.ArrayList;
import java.util.List;

@Tags({DistributedTags.SERVER, DistributedTags.RMI, DistributedTags.GIPC})
public class CoupledHalloweenSimulationsGIPCServer extends CoupledHalloweenSimulationsRMIServer {
    private static CoupledHalloweenSimulationsGIPCServer serverInstance = new CoupledHalloweenSimulationsGIPCServer();
    private List<RemoteClient> registeredGIPCClients;
    private GIPCRegistry gipcRegistry;

    CoupledHalloweenSimulationsGIPCServer() {
        this.registeredGIPCClients = new ArrayList<>();
        this.setIPCMechanism(IPCMechanism.GIPC);
    }

    @Override
    public void setTracing() {
        FactoryTraceUtility.setTracing();
        BeanTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        GIPCRPCTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        super.trace(true);
    }

    protected void init(String[] args) {
        this.setTracing();
        super.init(args);
        int gipcServerPort = ServerArgsProcessor.getGIPCServerPort(args);
        this.gipcRegistry = GIPCLocateRegistry.createRegistry(gipcServerPort);
        GIPCRegistryCreated.newCase(this, gipcServerPort);
        this.gipcRegistry.rebind(RemoteServer.class.getName(), this);
        GIPCObjectRegistered.newCase(this, RemoteServer.class.getName(), this, this.gipcRegistry);
        InputPort gipcPort = this.gipcRegistry.getInputPort();
        ConnectionListener listener = new ATracingConnectionListener(gipcPort);
        gipcPort.addConnectionListener(listener);
    }

    @Override
    public void registerClients() {
        RemoteClient clientProxy = (RemoteClient) this.gipcRegistry.lookup(RemoteClient.class, RemoteClient.class.getName());
        GIPCObjectLookedUp.newCase(this, clientProxy, RemoteClient.class, RemoteClient.class.getName(), this.gipcRegistry);
        this.registeredGIPCClients.add(clientProxy);
    }

    @Override
    public void ipcMechanism(IPCMechanism newValue) {
        this.setIPCMechanism(newValue);
        ProposalMade.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newValue);
    }

    @Override
    public void start(String[] args) {
        this.init(args);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    public static CoupledHalloweenSimulationsGIPCServer getSingleton() {
        return serverInstance;
    }

    public static void main(String[] args) {
        CoupledHalloweenSimulationsGIPCServer serverInstance = getSingleton();
        serverInstance.start(args);
    }
}
