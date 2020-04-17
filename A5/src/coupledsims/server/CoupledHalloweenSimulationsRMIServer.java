package coupledsims.server;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ServerArgsProcessor;
import coupledsims.client.RemoteClient;
import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

@Tags({DistributedTags.SERVER, DistributedTags.RMI})
public class CoupledHalloweenSimulationsRMIServer extends AnAbstractSimulationParametersBean implements Server, RemoteServer {
    private static CoupledHalloweenSimulationsRMIServer serverInstance = new CoupledHalloweenSimulationsRMIServer();
    private List<RemoteClient> registeredRMIClients;
    private Registry rmiRegistry;

    CoupledHalloweenSimulationsRMIServer() {
        this.registeredRMIClients = new ArrayList<>();
    }

    public static CoupledHalloweenSimulationsRMIServer getSingleton() {
        return serverInstance;
    }

    @Override
    public void quit(int aCode) {
        System.exit(aCode);
    }

    public void setTracing() {
        FactoryTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        this.trace(true);
    }

    public void locateRegistry(int registryPort, String registryHost) throws RemoteException {
        this.rmiRegistry = LocateRegistry.getRegistry(registryPort);
        RMIRegistryLocated.newCase(this, registryHost, registryPort, this.rmiRegistry);
    }

    public void exportServerProxy(int rmiServerPort) throws RemoteException {
        UnicastRemoteObject.exportObject(this, rmiServerPort);
        this.rmiRegistry.rebind(RemoteServer.class.getName(), this);
        RMIObjectRegistered.newCase(this, RemoteServer.class.getName(), this, this.rmiRegistry);
    }

    public void registerClients() {
        try {
            RemoteClient clientProxy = (RemoteClient) this.rmiRegistry.lookup(RemoteClient.class.getName());
            RMIObjectLookedUp.newCase(this, clientProxy, RemoteClient.class.getName(), this.rmiRegistry);
            this.registeredRMIClients.add(clientProxy);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    public void receiveRemoteProposeRequest(String aCommand, RemoteClient clientProxy) {
        RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, aCommand);
        for (RemoteClient aClientProxy: this.registeredRMIClients) {
            if (!aClientProxy.equals(clientProxy)) {
                try {
                    ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, aCommand);
                    boolean atomicBroadcastStatus = this.isAtomicBroadcast();
                    aClientProxy.receiveProposalLearnedNotification(aCommand, atomicBroadcastStatus);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    protected void init(String[] args) {
        try {
            this.setTracing();
            int rmiRegistryPort = ServerArgsProcessor.getRegistryPort(args), rmiServerPort = ServerArgsProcessor.getServerPort(args);
            String registryHost = ServerArgsProcessor.getRegistryHost(args);
            this.locateRegistry(rmiRegistryPort, registryHost);
            this.exportServerProxy(rmiServerPort);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void start(String[] args) {
        this.init(args);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    public static void main(String[] args) {
        CoupledHalloweenSimulationsRMIServer serverInstance = getSingleton();
        serverInstance.start(args);
    }
}
