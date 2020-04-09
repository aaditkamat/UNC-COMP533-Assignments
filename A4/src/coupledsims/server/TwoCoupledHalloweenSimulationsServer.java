package coupledsims.server;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ServerArgsProcessor;
import coupledsims.client.RemoteClient;
import util.annotations.Tags;
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
public class TwoCoupledHalloweenSimulationsServer extends AnAbstractSimulationParametersBean implements Server, RemoteServer {
    private static TwoCoupledHalloweenSimulationsServer serverInstance = new TwoCoupledHalloweenSimulationsServer();;
    private List<RemoteClient> registeredClients;
    private Registry rmiRegistry;

    TwoCoupledHalloweenSimulationsServer() {
        this.registeredClients = new ArrayList<>();
    }

    public static TwoCoupledHalloweenSimulationsServer getSingleton() {
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
        trace(true);
    }

    public void locateRegistry(int registryPort, String registryHost) throws RemoteException {
        this.rmiRegistry = LocateRegistry.getRegistry(registryPort);
        RMIRegistryLocated.newCase(this, registryHost, registryPort, this.rmiRegistry);
    }

    public void exportServerProxy(int serverPort) throws RemoteException {
        UnicastRemoteObject.exportObject(this, serverPort);
        this.rmiRegistry.rebind(RemoteServer.class.getName(), this);
        RMIObjectRegistered.newCase(this, RemoteServer.class.getName(), this, rmiRegistry);
    }

    public void registerClients() {
        try {
            RemoteClient clientProxy = (RemoteClient) rmiRegistry.lookup(RemoteClient.class.getName());
            RMIObjectLookedUp.newCase(this, clientProxy, Server.class.getName(), rmiRegistry);
            this.registeredClients.add(clientProxy);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    public void receiveRemoteProposeRequest(String aCommand, RemoteClient clientProxy) {
        RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, aCommand);
        for (RemoteClient aClientProxy: this.registeredClients) {
            if (!aClientProxy.equals(clientProxy)) {
                try {
                    ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, aCommand);
                    boolean atomicBroadcastStatus = isAtomicBroadcast();
                    aClientProxy.receiveProposalLearnedNotification(aCommand, atomicBroadcastStatus);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    public static void main(String[] args) {
        try {
            int registryPort = ServerArgsProcessor.getRegistryPort(args), serverPort = ServerArgsProcessor.getServerPort(args);
            String registryHost = ServerArgsProcessor.getRegistryHost(args);
            TwoCoupledHalloweenSimulationsServer serverInstance = getSingleton();
            serverInstance.setTracing();
            serverInstance.locateRegistry(registryPort, registryHost);
            serverInstance.exportServerProxy(serverPort);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
