package coupledsims.server;

import assignments.util.inputParameters.SimulationParametersListener;
import coupledsims.client.Client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface Server extends SimulationParametersListener {
    void setTracing();
    void start(String[] args);
}
