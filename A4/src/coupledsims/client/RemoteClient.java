package coupledsims.client;

import assignments.util.inputParameters.SimulationParametersListener;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClient extends Remote {
    void trace(boolean newValue) throws RemoteException;
}
