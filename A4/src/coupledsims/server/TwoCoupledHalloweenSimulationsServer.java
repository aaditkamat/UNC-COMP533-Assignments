package coupledsims.server;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ServerArgsProcessor;
import coupledsims.client.RemoteClient;
import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.Tracer;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

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

    TwoCoupledHalloweenSimulationsServer() {
        this.registeredClients = new ArrayList<>();
    }

    public static TwoCoupledHalloweenSimulationsServer getSingleton() {
        return serverInstance;
    }

    @Override
    public void trace(boolean newValue) {
        super.trace(newValue);
        Tracer.showInfo(isTrace());
    }

    public void setTracing() {
        FactoryTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        trace(true);
    }

    public Registry locateRegistry(int registryPort, String registryHost) throws RemoteException {
        Registry rmiRegistry = LocateRegistry.getRegistry(registryPort);
        RMIRegistryLocated.newCase(this, registryHost, registryPort, rmiRegistry);
        return rmiRegistry;
    }

    public void exportServerProxy(int serverPort, Registry rmiRegistry) throws RemoteException {
        UnicastRemoteObject.exportObject(this, serverPort);
        rmiRegistry.rebind(RemoteServer.class.getName(), this);
        RMIObjectRegistered.newCase(this, RemoteServer.class.getName(), this, rmiRegistry);
    }

    public void registerClients(RemoteClient clientProxy) {
        this.registeredClients.add(clientProxy);
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

    @Override
    public void atomicBroadcast(boolean newValue) {
        super.atomicBroadcast(newValue);
    }

    public static void main(String[] args) {
        try {
            int registryPort = ServerArgsProcessor.getRegistryPort(args), serverPort = ServerArgsProcessor.getServerPort(args);
            String registryHost = ServerArgsProcessor.getRegistryHost(args);
            TwoCoupledHalloweenSimulationsServer serverInstance = getSingleton();
            serverInstance.setTracing();
            Registry rmiRegistry = serverInstance.locateRegistry(registryPort, registryHost);
            serverInstance.exportServerProxy(serverPort, rmiRegistry);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
