package coupledsims.client;

import assignments.util.inputParameters.SimulationParametersListener;
import coupledsims.server.RemoteServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface Client extends SimulationParametersListener {
    void setTracing();
    void locateRegistry(int registryPort, String registryHost);
    void lookupRMIObject(String[] args);
    void start();
    void simulationCommand(String aCommand);
}
