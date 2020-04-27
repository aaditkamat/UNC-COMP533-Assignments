package coupledsims.client;

import java.rmi.RemoteException;

public interface GIPCClient {
    String CLIENT_NAME="Client ";
    void receiveProposalLearnedNotificationViaGIPC(String anObjectName, Object aProposal) throws RemoteException;
}
