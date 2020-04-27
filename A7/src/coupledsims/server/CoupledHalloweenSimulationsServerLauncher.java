package coupledsims.server;

import util.annotations.Tags;
import util.tags.DistributedTags;

@Tags({DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO, DistributedTags.SERVER})
public class CoupledHalloweenSimulationsServerLauncher {
    public static void main(String[] args) {
        CoupledHalloweenSimulationsRMIAndGIPCServer serverInstance = CoupledHalloweenSimulationsRMIGIPCAndNIOServer.getSingleton();
        serverInstance.start(args);
    }
}
