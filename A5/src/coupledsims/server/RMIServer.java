package coupledsims.server;

import coupledsims.client.RMIClient;
import util.interactiveMethodInvocation.IPCMechanism;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServer extends Remote, Serializable {
    void setTracing() throws RemoteException;
    void locateRegistry(int registryPort, String registryHost) throws RemoteException;
    void exportServerProxy(int serverPort) throws RemoteException;
    void quit(int aCode) throws RemoteException;
    void registerRMIClients() throws RemoteException;
    void receiveCommandViaRMI(String aCommand, RMIClient clientProxy) throws RemoteException;
    void receiveIPCMechanismViaRMI(IPCMechanism newValue, RMIClient clientProxy) throws RemoteException;
}
