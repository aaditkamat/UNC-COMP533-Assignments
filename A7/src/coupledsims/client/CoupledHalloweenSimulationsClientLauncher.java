package coupledsims.client;

import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;

@Tags({DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO, DistributedTags.CLIENT})
public class CoupledHalloweenSimulationsClientLauncher {
    public static void main(String[] args) {
        CoupledHalloweenSimulationsRemoteClient clientInstance = CoupledHalloweenSimulationsRemoteClient.getSingleton();
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(clientInstance);
        clientInstance.start(args);
    }
}
