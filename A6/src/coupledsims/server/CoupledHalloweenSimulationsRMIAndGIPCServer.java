package coupledsims.server;

import assignments.util.mainArgs.ServerArgsProcessor;
import coupledsims.client.GIPCClient;
import inputport.ConnectionListener;
import inputport.InputPort;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import port.ATracingConnectionListener;
import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.gipc.GIPCObjectLookedUp;
import util.trace.port.rpc.gipc.GIPCObjectRegistered;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryCreated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@Tags({DistributedTags.SERVER, DistributedTags.RMI, DistributedTags.GIPC})
public class CoupledHalloweenSimulationsRMIAndGIPCServer extends CoupledHalloweenSimulationsRMIServer implements GIPCServer{
    private static CoupledHalloweenSimulationsRMIAndGIPCServer serverInstance = new CoupledHalloweenSimulationsRMIAndGIPCServer();
    private List<GIPCClient> registeredGIPCClients;
    private GIPCRegistry gipcRegistry;

    public CoupledHalloweenSimulationsRMIAndGIPCServer() {
        this.registeredGIPCClients = new ArrayList<>();
    }

    public static CoupledHalloweenSimulationsRMIAndGIPCServer getSingleton() {
        return serverInstance;
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

    @Override
    protected void init(String[] args) {
        super.init(args);
        this.setTracing();
        int gipcServerPort = ServerArgsProcessor.getGIPCServerPort(args);
        this.gipcRegistry = GIPCLocateRegistry.createRegistry(gipcServerPort);
        GIPCRegistryCreated.newCase(this, gipcServerPort);
        this.gipcRegistry.rebind(GIPCServer.class.getName(), this);
        GIPCObjectRegistered.newCase(this, RMIServer.class.getName(), this, this.gipcRegistry);
        InputPort gipcPort = this.gipcRegistry.getInputPort();
        ConnectionListener listener = new ATracingConnectionListener(gipcPort);
        gipcPort.addConnectionListener(listener);
    }

    @Override
    public void start(String[] args) {
        this.init(args);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    protected void receiveRequestViaGIPC(String aCommand, GIPCClient currentClientProxy) {
        try {
            RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, aCommand);
            for (GIPCClient otherClientProxy : this.registeredGIPCClients) {
                if (!otherClientProxy.equals(currentClientProxy)) {
                    ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, aCommand);
                    otherClientProxy.receiveProposalLearnedNotificationViaGIPC(CommunicationStateNames.COMMAND, aCommand);
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void receiveCommandViaGIPC(String aCommand, GIPCClient currentClientProxy) {
        this.receiveRequestViaGIPC(aCommand, currentClientProxy);
    }

    public void registerGIPCClients() {
        GIPCClient clientProxy = (GIPCClient) this.gipcRegistry.lookup(GIPCClient.class, GIPCClient.class.getName());
        GIPCObjectLookedUp.newCase(this, clientProxy, GIPCClient.class, GIPCClient.class.getName(), this.gipcRegistry);
        this.registeredGIPCClients.add(clientProxy);
    }

    public static void main(String[] args) {
        CoupledHalloweenSimulationsRMIAndGIPCServer serverInstance = getSingleton();
        serverInstance.start(args);
    }
}
