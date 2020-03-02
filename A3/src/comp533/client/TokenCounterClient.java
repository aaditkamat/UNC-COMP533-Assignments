package comp533.client;


import comp533.keyvalue.KeyValue;
import comp533.mvc.RemoteTokenCounter;
import comp533.mvc.TokenCounter;
import comp533.reducer.Reducer;
import comp533.reducer.TokenCounterReducerFactory;
import comp533.mvc.DistributedTokenCounter;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

public class TokenCounterClient extends AMapReduceTracer implements Client {
    public static void main(String[] args) {
        try {
            Registry rmiRegistry = LocateRegistry.getRegistry(1099);
            RemoteTokenCounter counter1 = (RemoteTokenCounter) rmiRegistry.lookup(RemoteTokenCounter.class.getName());
            TokenCounterClient remoteClient = new TokenCounterClient();
            UnicastRemoteObject.exportObject(remoteClient, 0);
            rmiRegistry.rebind(TokenCounterClient.class.getName(), remoteClient);
            counter1.registerClient(remoteClient);
            remoteClient.synchronizedWait();
            AMapReduceTracer.traceExit(TokenCounter.class);
            System.exit(0);
        } catch (RemoteException | NotBoundException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public Map<String, Integer> reduce(List<KeyValue<String, Integer>> serializableKeyValuePairs) throws RemoteException {
        this.traceRemoteList(serializableKeyValuePairs);
        Reducer<String, Integer> reducer = TokenCounterReducerFactory.getReducer();
        Map<String, Integer> result = reducer.reduce(serializableKeyValuePairs);
        this.traceRemoteResult(result);
        return result;
    }

    public void quit() {
        this.traceQuit();
        this.synchronizedNotify();
    }
}
