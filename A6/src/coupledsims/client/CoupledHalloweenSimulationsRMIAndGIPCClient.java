package coupledsims.client;

import assignments.util.mainArgs.ClientArgsProcessor;
import coupledsims.server.GIPCServer;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.trace.bean.BeanTraceUtility;
import util.trace.port.consensus.RemoteProposeRequestSent;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.rpc.gipc.GIPCObjectLookedUp;
import util.trace.port.rpc.gipc.GIPCObjectRegistered;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryLocated;

public class CoupledHalloweenSimulationsRMIAndGIPCClient extends CoupledHalloweenSimulationsRMIClient implements GIPCClient{
    private static final CoupledHalloweenSimulationsRMIAndGIPCClient clientInstance = new CoupledHalloweenSimulationsRMIAndGIPCClient();
    private static int ctr = 0;
    private GIPCRegistry gipcRegistry;
    protected GIPCServer serverGIPCProxy;

    public CoupledHalloweenSimulationsRMIAndGIPCClient() {
        ctr++;
    }
    public static CoupledHalloweenSimulationsRMIAndGIPCClient getSingleton() {
        return clientInstance;
    }

    @Override
    public void setTracing() {
        super.setTracing();
        BeanTraceUtility.setTracing();
        GIPCRPCTraceUtility.setTracing();
    }

    @Override
    protected void init(String[] args) {
        this.setTracing();
        super.init(args);
        int gipcPort = ClientArgsProcessor.getGIPCPort(args);
        String gipcHostName = ClientArgsProcessor.getServerHost(args);
        this.locateGIPCRegistry(gipcPort, gipcHostName);
        this.lookupGIPCServerProxy();
        this.exportGIPCClientProxy();
        this.serverGIPCProxy.registerGIPCClients();
    }

    public void receiveProposalLearnedNotificationViaGIPC(String aCommand, Object aProposal) {
        this.receiveProposalLearnedNotification(CommunicationStateNames.COMMAND, aCommand);
    }


    protected void sendCommandViaGIPC(String aCommand) {
        this.commandProcessor.setInputString(aCommand);
        RemoteProposeRequestSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, ipcMechanism);
        this.serverGIPCProxy.receiveCommandViaGIPC(aCommand, this);
    }

    @Override
    public void simulationCommand(String aCommand) {
        if (this.ipcMechanism.equals(IPCMechanism.RMI)) {
            super.simulationCommand(aCommand);
        } else {
            this.sendCommandViaGIPC(aCommand);
        }
    }

    protected void locateGIPCRegistry(int gipcPort, String gipcHostName) {
        this.gipcRegistry = GIPCLocateRegistry.getRegistry(gipcHostName, gipcPort, GIPCClient.CLIENT_NAME + ctr);
        GIPCRegistryLocated.newCase(this, gipcHostName,gipcPort, GIPCClient.class.getName());
    }

    protected void lookupGIPCServerProxy() {
        this.serverGIPCProxy = (GIPCServer) gipcRegistry.lookup(GIPCServer.class, GIPCServer.class.getName());
        GIPCObjectLookedUp.newCase(this, this.serverGIPCProxy, GIPCServer.class, GIPCServer.class.getName(), this.gipcRegistry);
    }

    protected void exportGIPCClientProxy() {
        this.gipcRegistry.rebind(GIPCClient.class.getName(), this);
        GIPCObjectRegistered.newCase(this, GIPCClient.class.getName(), this, this.gipcRegistry);
    }

    @Override
    public void start(String[] args) {
        this.init(args);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }
}
