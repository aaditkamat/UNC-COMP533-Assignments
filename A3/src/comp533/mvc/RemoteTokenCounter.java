package comp533.mvc;

import comp533.client.Client;
import comp533.view.View;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteTokenCounter extends Remote, Serializable {
    void registerClient(Client clientToRegister) throws RemoteException;
    void setNumThreads(int numThreads, View view) throws RemoteException;
    void setInputString(String newInputString, View view) throws RemoteException;
    void interruptThreads() throws RemoteException;
    void callClientQuit() throws RemoteException;
}
