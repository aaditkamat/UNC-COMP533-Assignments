package comp533.slave;

import comp533.barrier.Barrier;
import comp533.client.Client;
import comp533.joiner.Joiner;
import comp533.keyvalue.KeyValue;
import comp533.keyvalue.TokenCounterKeyValue;
import comp533.mvc.TokenCounter;
import comp533.partitioner.TokenCounterPartitioner;
import comp533.partitioner.TokenCounterPartitionerFactory;
import comp533.reducer.Reducer;
import comp533.reducer.TokenCounterReducerFactory;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TokenCounterSlave extends AMapReduceTracer implements Slave {
    private int threadId;
    private Client client;
    private List<KeyValue<String, Integer>> keyValueList;
    private TokenCounter counter;

    public TokenCounterSlave(int threadId, TokenCounter counter) {
        this.threadId = threadId;
        this.counter = counter;
        this.keyValueList = new ArrayList<>();
    }

    public synchronized void notifySlave() {
        this.synchronizedNotify();
    }

    public void splitBoundedBuffer() throws InterruptedException {
        ArrayBlockingQueue<KeyValue<String, Integer>> boundedBuffer = this.counter.getBoundedBuffer();
        KeyValue<String, Integer> consumedItem = null;
        while(consumedItem == null || consumedItem.getKey() != null) {
            this.traceDequeueRequest(boundedBuffer);
            consumedItem = boundedBuffer.take();
            this.traceDequeue(consumedItem);
            if (consumedItem.getKey() != null) {
                this.keyValueList.add(consumedItem);
            }
        }
    }

    public Map<String, Integer> reduceList(Reducer<String, Integer> reducer, List<KeyValue<String, Integer>> keyValuePairs) {
        try {
            this.traceRemoteList(keyValuePairs);
            return this.client.reduce(keyValuePairs);
        } catch (RemoteException | NullPointerException ex) {
            return reducer.reduce(keyValuePairs);
        }

    }

    private ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> splitReduction(Map<String, Integer> partiallyReducedMap) {
        ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> reduceQueueList = this.counter.getReductionQueueList();
        TokenCounterPartitioner partitioner = TokenCounterPartitionerFactory.getPartitioner();
        for (Map.Entry<String, Integer> entry: partiallyReducedMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (key == null) {
                break;
            }
            int numOfPartitions = reduceQueueList.size();
            int index = partitioner.getPartition(key, value, numOfPartitions);
            this.tracePartitionAssigned(key, value, index, numOfPartitions);
            KeyValue<String, Integer> keyValue = new TokenCounterKeyValue<>(key, value);
            reduceQueueList.get(index).add(keyValue);
        }
        return reduceQueueList;
    }

    public void setClient(Client client) {
        this.client = client;
        this.traceClientAssignment(client);
    }

    public void signalQuit() {
        this.traceQuit();
    }

    @Override
    public void run() {
        Barrier tokenCounterBarrier = this.counter.getBarrier();
        Reducer<String, Integer> reducer = TokenCounterReducerFactory.getReducer();
        while(true) {
            try {
                Map<String, Integer> originalMap = this.counter.getResult();
                this.splitBoundedBuffer();
                Map<String, Integer> partiallyReducedMap = this.reduceList(reducer, this.keyValueList);
                ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> reductionQueueList = this.splitReduction(partiallyReducedMap);
                tokenCounterBarrier.barrier();
                this.traceSplitAfterBarrier(this.threadId, reductionQueueList);
                for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue : reductionQueueList) {
                    List<KeyValue<String, Integer>> keyValues = List.copyOf(reductionQueue);
                    this.reduceList(reducer, keyValues);
                }
                Joiner joiner = this.counter.getJoiner();
                joiner.finished();
                this.traceAddedToMap(originalMap, this.counter.getResult());
                this.synchronizedWait();
            } catch (InterruptedException ex) {
                Tracer.error(Arrays.toString(ex.getStackTrace()));
                break;
            }
        }
    }
}
