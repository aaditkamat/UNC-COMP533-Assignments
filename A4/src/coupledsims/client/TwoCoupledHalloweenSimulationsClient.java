package coupledsims.client;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.inputParameters.SimulationParametersListener;
import assignments.util.mainArgs.ClientArgsProcessor;
import coupledsims.model.RemoteStandAloneTwoCoupledHalloweenSimulations;
import coupledsims.registry.TwoCoupledHalloweenSimulationsRMIRegistry;
import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.Tracer;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.PortTraceUtility;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.ProposalMade;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.RemoteProposeRequestSent;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.lang.reflect.Method;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Tags({DistributedTags.CLIENT, DistributedTags.RMI})
public class TwoCoupledHalloweenSimulationsClient extends AnAbstractSimulationParametersBean implements RemoteClient, SimulationParametersListener {
    private static TwoCoupledHalloweenSimulationsClient clientInstance;

    public static TwoCoupledHalloweenSimulationsClient getSingleton() {
        return clientInstance;
    }

    protected void setTracing() {
        PortTraceUtility.setTracing();
        FactoryTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        trace(true);
    }

    public void start() {
        setTracing();
//        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
//        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    @Override
    public void trace(boolean newValue) {
        super.trace(newValue);
        Tracer.showInfo(isTrace());
    }

    protected Registry locateRegistry(int registryPort, String registryHost) throws RemoteException {
        Registry rmiRegistry = LocateRegistry.getRegistry(registryPort);
        RMIRegistryLocated.newCase(this, registryHost, registryPort, rmiRegistry);
        return rmiRegistry;
    }

    protected RemoteStandAloneTwoCoupledHalloweenSimulations lookupRMIObject(Registry rmiRegistry, String[] args) throws RemoteException, NotBoundException {
        RemoteStandAloneTwoCoupledHalloweenSimulations simulation = (RemoteStandAloneTwoCoupledHalloweenSimulations) rmiRegistry.lookup(RemoteStandAloneTwoCoupledHalloweenSimulations.class.getName());
        RMIObjectLookedUp.newCase(this, simulation, RemoteStandAloneTwoCoupledHalloweenSimulations.class.getName(), rmiRegistry);
        return simulation;
    }

    protected void sendProposal(RemoteStandAloneTwoCoupledHalloweenSimulations simulation, String[] args) {
        Method newCommand = simulation.getClass().getMethods()[0];
        ProposalMade.newCase(this, RemoteStandAloneTwoCoupledHalloweenSimulations.class.getName(), -1, simulation);
        RemoteProposeRequestSent.newCase(this, CommunicationStateNames.COMMAND, -1, newCommand);
    }

    protected void registerRMIObject(Registry rmiRegistry, int serverPort) throws RemoteException {
        UnicastRemoteObject.exportObject(this, serverPort);
        rmiRegistry.rebind(TwoCoupledHalloweenSimulationsClient.class.getName(), this);
    }

    public static void main(String[] args) {
        try {
            int registryPort = ClientArgsProcessor.getRegistryPort(args), serverPort = ClientArgsProcessor.getServerPort(args);
            String registryHost = ClientArgsProcessor.getRegistryHost(args);
            clientInstance = new TwoCoupledHalloweenSimulationsClient();
            clientInstance.start();
            Registry rmiRegistry = clientInstance.locateRegistry(registryPort, registryHost);
            RemoteStandAloneTwoCoupledHalloweenSimulations simulation = clientInstance.lookupRMIObject(rmiRegistry, args);
            clientInstance.registerRMIObject(rmiRegistry, serverPort);
            clientInstance.sendProposal(simulation, args);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }
    }

}
