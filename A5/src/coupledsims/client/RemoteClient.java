package coupledsims.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClient extends Remote, Serializable {
    void receiveProposalLearnedNotification(String aCommand, boolean atomicBroadcastStatus) throws RemoteException;
    void quit(int aCode) throws RemoteException;
}
