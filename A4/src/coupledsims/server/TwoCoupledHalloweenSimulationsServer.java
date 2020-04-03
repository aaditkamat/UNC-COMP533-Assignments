package coupledsims.server;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.inputParameters.SimulationParametersListener;
import assignments.util.mainArgs.ServerArgsProcessor;
import coupledsims.model.AStandAloneTwoCoupledHalloweenSimulations;
import coupledsims.model.RemoteStandAloneTwoCoupledHalloweenSimulations;
import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Tags({DistributedTags.SERVER, DistributedTags.RMI})
public class TwoCoupledHalloweenSimulationsServer extends AnAbstractSimulationParametersBean implements SimulationParametersListener {
    private static TwoCoupledHalloweenSimulationsServer serverInstance;

    public static TwoCoupledHalloweenSimulationsServer getSingleton() {
        return serverInstance;
    }

    static void setTracing() {
        FactoryTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
    }

    protected Registry locateRegistry(int registryPort, String registryHost) throws RemoteException {
        Registry rmiRegistry = LocateRegistry.getRegistry(registryPort);
        RMIRegistryLocated.newCase(serverInstance, registryHost, registryPort, rmiRegistry);
        return rmiRegistry;
    }

    protected RemoteStandAloneTwoCoupledHalloweenSimulations registerRMIObject(int serverPort, Registry rmiRegistry) throws RemoteException {
        RemoteStandAloneTwoCoupledHalloweenSimulations simulation = new AStandAloneTwoCoupledHalloweenSimulations();
        UnicastRemoteObject.exportObject(simulation, serverPort);
        rmiRegistry.rebind(RemoteStandAloneTwoCoupledHalloweenSimulations.class.getName(), simulation);
        RMIObjectRegistered.newCase(serverInstance, RemoteStandAloneTwoCoupledHalloweenSimulations.class.getName(), simulation, rmiRegistry);
        return simulation;
    }

    public static void main(String[] args) {
        try {
            setTracing();
            int registryPort = ServerArgsProcessor.getRegistryPort(args), serverPort = ServerArgsProcessor.getServerPort(args);
            String registryHost = ServerArgsProcessor.getRegistryHost(args);
            serverInstance = new TwoCoupledHalloweenSimulationsServer();
            Registry rmiRegistry = serverInstance.locateRegistry(registryPort, registryHost);
            RemoteStandAloneTwoCoupledHalloweenSimulations simulation = serverInstance.registerRMIObject(serverPort, rmiRegistry);
            simulation.start(args);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
