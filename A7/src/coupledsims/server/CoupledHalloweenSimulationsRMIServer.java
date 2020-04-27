package coupledsims.server;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ServerArgsProcessor;
import coupledsims.client.RMIClient;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CoupledHalloweenSimulationsRMIServer extends AnAbstractSimulationParametersBean implements Server, RMIServer {
    private static final CoupledHalloweenSimulationsRMIServer serverInstance = new CoupledHalloweenSimulationsRMIServer();
    protected List<RMIClient> registeredRMIClients;
    protected Registry rmiRegistry;

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

    @Override
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

    public void exportServerProxy() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
        this.rmiRegistry.rebind(RMIServer.class.getName(), this);
        RMIObjectRegistered.newCase(this, RMIServer.class.getName(), this, this.rmiRegistry);
    }

    public void registerRMIClients(String lookupName) {
        try {
            RMIClient clientProxy = (RMIClient) this.rmiRegistry.lookup(lookupName);
            RMIObjectLookedUp.newCase(this, clientProxy, lookupName, this.rmiRegistry);
            this.registeredRMIClients.add(clientProxy);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    protected void traceRemoteProposeRequestReceived(String anObjectName, Object aProposal) {
        RemoteProposeRequestReceived.newCase(this, anObjectName, -1, aProposal);
    }

    protected <T extends RMIClient> void sendProposalToOtherClients(String anObjectName, Object aProposal, List<T> registeredClients, T currentClientProxy) {
        try {
            for (T otherClientProxy : registeredClients) {
                if (!otherClientProxy.equals(currentClientProxy)) {
                    ProposalLearnedNotificationSent.newCase(this, anObjectName, -1, aProposal);
                    otherClientProxy.receiveProposalLearnedNotificationViaRMI(anObjectName, aProposal);
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private void receiveRequestViaRMI(String anObjectName, java.io.Serializable aProposal, RMIClient currentClientProxy) {
        this.traceRemoteProposeRequestReceived(anObjectName, aProposal);
        this.sendProposalToOtherClients(anObjectName, aProposal, this.registeredRMIClients, currentClientProxy);
    }

    public void receiveCommandViaRMI(String aCommand, RMIClient clientProxy) {
        this.receiveRequestViaRMI(CommunicationStateNames.COMMAND, aCommand, clientProxy);
    }

    public void receiveIPCMechanismViaRMI(IPCMechanism ipcMechanism, RMIClient clientProxy) {
        this.receiveRequestViaRMI(CommunicationStateNames.IPC_MECHANISM, ipcMechanism, clientProxy);
    }

    public void receiveBroadcastStateViaRMI(boolean isAtomicBroadcast, RMIClient clientProxy) {
        this.receiveRequestViaRMI(CommunicationStateNames.BROADCAST_MODE, isAtomicBroadcast, clientProxy);
    }

    protected void init(String[] args) {
        try {
            this.setTracing();
            int rmiRegistryPort = ServerArgsProcessor.getRegistryPort(args);
            String registryHost = ServerArgsProcessor.getRegistryHost(args);
            this.locateRegistry(rmiRegistryPort, registryHost);
            this.exportServerProxy();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void start(String[] args) {
        this.init(args);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }
}
