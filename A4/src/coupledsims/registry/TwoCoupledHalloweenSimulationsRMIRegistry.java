package coupledsims.registry;

import assignments.util.mainArgs.RegistryArgsProcessor;
import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.port.rpc.rmi.RMIRegistryCreated;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;


@Tags({DistributedTags.REGISTRY, DistributedTags.RMI})
public class TwoCoupledHalloweenSimulationsRMIRegistry {
    public static void main(String[] args) {
        try {
            RMITraceUtility.setTracing();
            int port = RegistryArgsProcessor.getRegistryPort(args);
            Registry registry = LocateRegistry.createRegistry(port);
            RMIRegistryCreated.newCase(registry, port);
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

}
