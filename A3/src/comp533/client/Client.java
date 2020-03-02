package comp533.client;

import comp533.keyvalue.KeyValue;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface Client extends Remote, Serializable {
    Map<String, Integer> reduce(List<KeyValue<String, Integer>> serializableKeyValuePairs) throws RemoteException;
    void quit() throws RemoteException;
}
