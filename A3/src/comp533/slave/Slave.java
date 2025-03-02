package comp533.slave;

import comp533.client.Client;
import comp533.keyvalue.KeyValue;
import comp533.reducer.Reducer;

import java.util.List;
import java.util.Map;

public interface Slave extends Runnable {
    void notifySlave();
    void splitBoundedBuffer() throws InterruptedException;
    Map<String, Integer> reduceList(Reducer<String, Integer> reducer, List<KeyValue<String, Integer>> keyValuePairs);
    void signalQuit();
    void setClient(Client client);
}
