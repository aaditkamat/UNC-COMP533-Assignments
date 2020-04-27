package coupledsims.server;

import coupledsims.client.RMIClient;
import coupledsims.client.RemoteClient;
import util.interactiveMethodInvocation.IPCMechanism;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServer extends Remote, Serializable {
    void setTracing() throws RemoteException;
    void locateRegistry(int registryPort, String registryHost) throws RemoteException;
    void exportServerProxy() throws RemoteException;
    void quit(int aCode) throws RemoteException;
    void registerRMIClients(String lookupName) throws RemoteException;
    void receiveCommandViaRMI(String aCommand, RMIClient clientProxy) throws RemoteException;
    void receiveIPCMechanismViaRMI(IPCMechanism newValue, RMIClient clientProxy) throws RemoteException;
    void receiveBroadcastStateViaRMI(boolean isAtomicBroadcast, RMIClient clientProxy) throws RemoteException;
}
