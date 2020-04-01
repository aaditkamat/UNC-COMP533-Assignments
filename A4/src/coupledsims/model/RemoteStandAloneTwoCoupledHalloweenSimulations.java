package coupledsims.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteStandAloneTwoCoupledHalloweenSimulations extends Remote {
	void start(String[] args) throws RemoteException;
	void trace(boolean newValue) throws RemoteException;
}
