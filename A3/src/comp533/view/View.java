package comp533.view;

import java.beans.PropertyChangeEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface View extends Remote {
    void propertyChange(PropertyChangeEvent event) throws RemoteException;
}
