package coupledsims.client;

import assignments.util.inputParameters.SimulationParametersListener;

public interface Client extends SimulationParametersListener {
    void setTracing();
    void locateRMIRegistry(int registryPort, String registryHost);
    void lookupRMIServerProxy();
    void exportRMIClientProxy();
    void start(String[] args);
    void simulationCommand(String aCommand);
}
