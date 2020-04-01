package coupledsims.client;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import assignments.util.mainArgs.ClientArgsProcessor;
import coupledsims.model.AStandAloneTwoCoupledHalloweenSimulations;
import coupledsims.model.RemoteStandAloneTwoCoupledHalloweenSimulations;
import coupledsims.registry.TwoCoupledHalloweenSimulationsRMIRegistry;
import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Tags({DistributedTags.CLIENT, DistributedTags.RMI})
public class TwoCoupledHalloweenSimulationsClient extends AnAbstractSimulationParametersBean implements Client {
    private static TwoCoupledHalloweenSimulationsClient clientInstance;

    public static TwoCoupledHalloweenSimulationsClient getSingleton() {
        return clientInstance;
    }

    static void setTracing() {
        FactoryTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        NIOTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
    }

    public static void main(String[] args) {
        try {
            setTracing();
            int registryPort = ClientArgsProcessor.getRegistryPort(args), serverPort = ClientArgsProcessor.getServerPort(args);
            String registryHost = ClientArgsProcessor.getRegistryHost(args);
            Registry rmiRegistry = LocateRegistry.getRegistry(registryPort);
            clientInstance = new TwoCoupledHalloweenSimulationsClient();
            RMIRegistryLocated.newCase(clientInstance, registryHost, ClientArgsProcessor.getRegistryPort(args), rmiRegistry);
            RemoteStandAloneTwoCoupledHalloweenSimulations simulation = (RemoteStandAloneTwoCoupledHalloweenSimulations) rmiRegistry.lookup(RemoteStandAloneTwoCoupledHalloweenSimulations.class.getName());
            RMIObjectLookedUp.newCase(clientInstance, simulation, AStandAloneTwoCoupledHalloweenSimulations.class.getName(), rmiRegistry);
            UnicastRemoteObject.exportObject(clientInstance, serverPort);
            rmiRegistry.rebind(TwoCoupledHalloweenSimulationsRMIRegistry.class.getName(), simulation);
        } catch (RemoteException | NotBoundException ex) {
            ex.getStackTrace();
        }
    }
}
