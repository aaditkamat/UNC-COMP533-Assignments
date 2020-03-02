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
import java.util.List;
import java.util.Map;

public class TokenCounterClient extends AMapReduceTracer implements Client {
    public static void main(String[] args) {
        try {
            Registry rmiRegistry = LocateRegistry.getRegistry(1099);
            RemoteTokenCounter counter1 = (RemoteTokenCounter) rmiRegistry.lookup(RemoteTokenCounter.class.getName());
            counter1.registerClient(new TokenCounterClient());
            AMapReduceTracer.traceExit(TokenCounter.class);
        } catch (RemoteException | NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    public Map<String, Integer> reduce(List<KeyValue<String, Integer>> serializableKeyValuePairs) {
        Reducer<String, Integer> reducer = TokenCounterReducerFactory.getReducer();
        return reducer.reduce(serializableKeyValuePairs);
    }

    public void terminateClients() {
        try {
            this.synchronizedWait();
            AMapReduceTracer.traceExit(TokenCounterClient.class);
            System.exit(0);
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }

    }

    public void quit() {
        this.synchronizedNotify();
        this.traceQuit();
    }
}
