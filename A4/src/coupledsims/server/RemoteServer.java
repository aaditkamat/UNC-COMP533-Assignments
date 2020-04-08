package coupledsims.server;

import coupledsims.client.Client;
import coupledsims.client.RemoteClient;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface RemoteServer extends Remote, Serializable {
    void setTracing() throws RemoteException;
    Registry locateRegistry(int registryPort, String registryHost) throws RemoteException;
    void exportServerProxy(int serverPort, Registry rmiRegistry) throws RemoteException;
    void registerClients(RemoteClient remoteClient) throws RemoteException;
    void receiveRemoteProposeRequest(String aCommand, RemoteClient clientProxy) throws RemoteException;
}
