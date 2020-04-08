package coupledsims.client;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ClientArgsProcessor;
import coupledsims.ASimulationCoupler;
import coupledsims.server.RemoteServer;
import coupledsims.server.Server;
import coupledsims.simulation.Simulation1;
import main.BeauAndersonFinalProject;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.misc.ThreadSupport;
import util.tags.DistributedTags;
import util.trace.Tracer;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.PortTraceUtility;
import util.trace.port.consensus.*;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.beans.PropertyChangeListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Tags({DistributedTags.CLIENT, DistributedTags.RMI})
public class TwoCoupledHalloweenSimulationsClient extends AnAbstractSimulationParametersBean implements Client, RemoteClient {
    HalloweenCommandProcessor commandProcessor;
    private static TwoCoupledHalloweenSimulationsClient clientInstance = new TwoCoupledHalloweenSimulationsClient();
    Registry rmiRegistry;
    RemoteServer serverProxy;
    protected PropertyChangeListener simulationCoupler;

    public static TwoCoupledHalloweenSimulationsClient getSingleton() {
        return clientInstance;
    }

    protected HalloweenCommandProcessor createSimulation(String aPrefix) {
        return 	BeauAndersonFinalProject.createSimulation(
                aPrefix,
                coupledsims.simulation.Simulation1.SIMULATION1_X_OFFSET,
                coupledsims.simulation.Simulation.SIMULATION_Y_OFFSET,
                coupledsims.simulation.Simulation.SIMULATION_WIDTH,
                coupledsims.simulation.Simulation.SIMULATION_HEIGHT,
                coupledsims.simulation.Simulation1.SIMULATION1_X_OFFSET,
                coupledsims.simulation.Simulation.SIMULATION_Y_OFFSET);
    }

    protected TwoCoupledHalloweenSimulationsClient() {
        this.commandProcessor = createSimulation(Simulation1.SIMULATION1_PREFIX);
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
        trace(true);
    }

    @Override
    public void start() {
        try {
            this.serverProxy.registerClients(this);
            setTracing();
            SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
            SimulationParametersControllerFactory.getSingleton().processCommands();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void simulationCommand(String aCommand) {
        long aDelay = getDelay();
        if (aDelay > 0) {
            ThreadSupport.sleep(aDelay);
        }
        ProposalMade.newCase(this, CommunicationStateNames.COMMAND, -1, aCommand);
        this.commandProcessor.setInputString(aCommand);
        RemoteProposeRequestSent.newCase(this, CommunicationStateNames.COMMAND, -1, aCommand);
        try {
            this.serverProxy.receiveRemoteProposeRequest(aCommand, this);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void receiveProposalLearnedNotification(String aCommand, boolean atomicBroadcastStatus) {
        ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, aCommand);
        setAtomicBroadcast(atomicBroadcastStatus);
        this.commandProcessor.setConnectedToSimulation(!isAtomicBroadcast());
        ProposedStateSet.newCase(this, CommunicationStateNames.COMMAND, -1, aCommand);
        this.commandProcessor.setInputString(aCommand);
    }

    @Override
    public void trace(boolean newValue) {
        super.trace(newValue);
        Tracer.showInfo(isTrace());
    }

    public void locateRegistry(int registryPort, String registryHost) {
        try {
            this.rmiRegistry = LocateRegistry.getRegistry(registryPort);
            RMIRegistryLocated.newCase(this, registryHost, registryPort, rmiRegistry);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void lookupRMIObject(String[] args) {
        try {
            this.serverProxy = (RemoteServer) rmiRegistry.lookup(RemoteServer.class.getName());
            RMIObjectLookedUp.newCase(this, this.serverProxy, Server.class.getName(), rmiRegistry);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int registryPort = ClientArgsProcessor.getRegistryPort(args);
        String registryHost = ClientArgsProcessor.getRegistryHost(args);
        TwoCoupledHalloweenSimulationsClient clientInstance = getSingleton();
        clientInstance.setTracing();
        clientInstance.locateRegistry(registryPort, registryHost);
        clientInstance.lookupRMIObject(args);
        clientInstance.start();
    }

}
