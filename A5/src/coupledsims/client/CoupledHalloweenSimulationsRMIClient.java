package coupledsims.client;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ClientArgsProcessor;
import coupledsims.ASimulationCoupler;
import coupledsims.server.RMIServer;
import main.BeauAndersonFinalProject;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.misc.ThreadSupport;
import util.tags.DistributedTags;
import util.trace.Tracer;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.PerformanceExperimentEnded;
import util.trace.port.PerformanceExperimentStarted;
import util.trace.port.PortTraceUtility;
import util.trace.port.consensus.*;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.beans.PropertyChangeListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Tags({DistributedTags.CLIENT, DistributedTags.RMI})
public class CoupledHalloweenSimulationsRMIClient extends AnAbstractSimulationParametersBean implements Client, RMIClient {
    private static final long serialVersionUID = 8681387667445501882L;
    protected HalloweenCommandProcessor commandProcessor;
    protected int NUM_EXPERIMENT_COMMANDS = 500;
    public static final String EXPERIMENT_COMMAND = "move 1 -1";
    private static CoupledHalloweenSimulationsRMIClient clientInstance = new CoupledHalloweenSimulationsRMIClient();
    protected Registry rmiRegistry;
    protected RMIServer serverRMIProxy;
    transient protected PropertyChangeListener simulationCoupler;
    static int numberOfClients = 0;

    public static CoupledHalloweenSimulationsRMIClient getSingleton() {
        return clientInstance;
    }

    protected HalloweenCommandProcessor createSimulation(String aPrefix) {
        return 	BeauAndersonFinalProject.createSimulation(
                aPrefix,
                coupledsims.simulation.Simulation.SIMULATION_X_OFFSET,
                coupledsims.simulation.Simulation.SIMULATION_Y_OFFSET,
                coupledsims.simulation.Simulation.SIMULATION_WIDTH,
                coupledsims.simulation.Simulation.SIMULATION_HEIGHT,
                coupledsims.simulation.Simulation.SIMULATION_X_OFFSET,
                coupledsims.simulation.Simulation.SIMULATION_Y_OFFSET);
    }

    protected CoupledHalloweenSimulationsRMIClient() {
        numberOfClients += 1;
        this.commandProcessor = createSimulation(numberOfClients + ":");
        this.simulationCoupler = new ASimulationCoupler(this.commandProcessor);
        this.commandProcessor.addPropertyChangeListener(this.simulationCoupler);
    }

    public void setTracing() {
        PortTraceUtility.setTracing();
        FactoryTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        this.trace(true);
    }

    protected void init(String[] args) {
        try {
            this.setTracing();
            int rmiPort = ClientArgsProcessor.getRegistryPort(args);
            String rmiRegistryHost = ClientArgsProcessor.getRegistryHost(args);
            this.locateRMIRegistry(rmiPort, rmiRegistryHost);
            this.lookupRMIServerProxy();
            this.exportRMIClientProxy();
            this.serverRMIProxy.registerRMIClients();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(String[] args) {
        this.init(args);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    protected void sendProposalViaRMI(Object aProposal, String stateName) {
        try {
            RemoteProposeRequestSent.newCase(this, stateName, -1, aProposal);
            if (stateName.equals(CommunicationStateNames.COMMAND)) {
                String aCommand = (String) aProposal;
                this.commandProcessor.setInputString(aCommand);
                this.serverRMIProxy.receiveCommandViaRMI(aCommand, this);
            } else {
                IPCMechanism ipcMechanism = (IPCMechanism) aProposal;
                this.serverRMIProxy.receiveIPCMechanismViaRMI(ipcMechanism, this);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    protected void sendCommandViaRMI(String aCommand) {
        this.sendProposalViaRMI(aCommand, CommunicationStateNames.COMMAND);
    }

    protected void sendIPCMechanismViaRMI(IPCMechanism ipcMechanism) {
        this.sendProposalViaRMI(ipcMechanism, CommunicationStateNames.IPC_MECHANISM);
    }

    @Override
    public void ipcMechanism(IPCMechanism newValue) {
        super.ipcMechanism(newValue);
//        this.sendIPCMechanismViaRMI(newValue);
    }

    @Override
    public void simulationCommand(String aCommand) {
        super.simulationCommand(aCommand);
        long aDelay = this.getDelay();
        if (aDelay > 0) {
            ThreadSupport.sleep(aDelay);
        }
        this.commandProcessor.setInputString(aCommand);
        this.sendCommandViaRMI(aCommand);
    }

    @Override
    public void trace(boolean newValue) {
        super.trace(newValue);
        Tracer.showInfo(isTrace());
    }

    @Override
    public synchronized void setAtomicBroadcast(Boolean newValue) {
        super.setAtomicBroadcast(newValue);
        this.commandProcessor.setConnectedToSimulation(!isAtomicBroadcast());
    }

    @Override
    public void quit(int aCode) {
        System.exit(aCode);
        try {
            this.serverRMIProxy.quit(aCode);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void localProcessingOnly(boolean newValue) {
        super.localProcessingOnly(newValue);
        if (this.isLocalProcessingOnly()) {
            this.commandProcessor.removePropertyChangeListener(simulationCoupler);
        } else {
            this.commandProcessor.addPropertyChangeListener(simulationCoupler);
        }
    }

    @Override
    public void experimentInput() {
        long aStartTime = System.currentTimeMillis();
        PerformanceExperimentStarted.newCase(this, aStartTime, NUM_EXPERIMENT_COMMANDS);
        boolean anOldValue = isTrace();
        this.trace(false);
        for (int i = 0; i < NUM_EXPERIMENT_COMMANDS; i++) {
            this.simulationCommand(EXPERIMENT_COMMAND);
        }
        this.trace(anOldValue);
        long anEndTime = System.currentTimeMillis();
        PerformanceExperimentEnded.newCase(this, aStartTime, anEndTime, anEndTime - aStartTime, NUM_EXPERIMENT_COMMANDS);
    }

    public void exportRMIClientProxy() {
        try {
            UnicastRemoteObject.exportObject(this, 0);
            this.rmiRegistry.rebind(RMIClient.class.getName(), this);
            RMIObjectRegistered.newCase(this, RMIClient.class.getName(), this, this.rmiRegistry);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    protected void receiveProposalLearnedNotification(String anObjectName, Object aProposal) {
        ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, anObjectName);
        ProposedStateSet.newCase(this, anObjectName, -1, aProposal);
        if (anObjectName.equals(CommunicationStateNames.COMMAND)) {
            String aCommand = (String) aProposal;
            HalloweenCommandProcessor commandProcessor = CoupledHalloweenSimulationsRMIClient.getSingleton().commandProcessor;
            commandProcessor.setInputString(aCommand);
        } else {
            IPCMechanism ipcMechanism = (IPCMechanism) aProposal;
            CoupledHalloweenSimulationsRMIClient.getSingleton().setIPCMechanism(ipcMechanism);
        }
    }

    public void receiveProposalLearnedNotificationViaRMI(String anObjectName, Object aProposal) {
        this.receiveProposalLearnedNotification(anObjectName, aProposal);
    }

    public void locateRMIRegistry(int registryPort, String registryHost) {
        try {
            this.rmiRegistry = LocateRegistry.getRegistry(registryPort);
            RMIRegistryLocated.newCase(this, registryHost, registryPort, this.rmiRegistry);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void lookupRMIServerProxy() {
        try {
            this.serverRMIProxy = (RMIServer) this.rmiRegistry.lookup(RMIServer.class.getName());
            RMIObjectLookedUp.newCase(this, this.serverRMIProxy, RMIServer.class.getName(), rmiRegistry);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CoupledHalloweenSimulationsRMIClient clientInstance = new CoupledHalloweenSimulationsRMIClient();
        clientInstance.start(args);
    }

}
