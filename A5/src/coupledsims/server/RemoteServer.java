package coupledsims.server;

import coupledsims.client.RemoteClient;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote, Serializable {
    void setTracing() throws RemoteException;
    void locateRegistry(int registryPort, String registryHost) throws RemoteException;
    void exportServerProxy(int serverPort) throws RemoteException;
    void quit(int aCode) throws RemoteException;
    void registerClients() throws RemoteException;
    void receiveRemoteProposeRequest(String aCommand, RemoteClient clientProxy) throws RemoteException;
}
