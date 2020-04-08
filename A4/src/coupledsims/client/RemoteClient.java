package coupledsims.client;

import assignments.util.inputParameters.SimulationParametersListener;
import coupledsims.server.Server;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface RemoteClient extends Remote, Serializable{
    void setTracing();
    void receiveProposalLearnedNotification(String aCommand, boolean atomicBroadcastStatus) throws RemoteException;
}
