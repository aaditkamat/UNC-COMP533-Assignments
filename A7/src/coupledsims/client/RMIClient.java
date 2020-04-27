package coupledsims.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClient extends Remote, Serializable {
    void receiveProposalLearnedNotificationViaRMI(String anObjectName, Object aProposal) throws RemoteException;
    void quit(int aCode) throws RemoteException;
}
