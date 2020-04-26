package coupledsims.client;

import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;

@Tags({DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO, DistributedTags.CLIENT})
public class CoupleHalloweenSimulationsClientLauncher {
    public static void main(String[] args) {
        CoupledHalloweenSimulationsRMIGIPCAndNIOClient clientInstance = CoupledHalloweenSimulationsRMIGIPCAndNIOClient.getSingleton();
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(clientInstance);
        clientInstance.start(args);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }
}
