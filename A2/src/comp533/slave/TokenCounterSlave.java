package comp533.slave;

import comp533.barrier.TokenCounterBarrier;
import comp533.joiner.Joiner;
import comp533.keyvalue.KeyValue;
import comp533.model.TokenCounterModel;
import comp533.partitioner.TokenCounterPartitioner;
import comp533.partitioner.TokenCounterPartitionerFactory;
import comp533.reducer.Reducer;
import comp533.reducer.TokenCounterReducerFactory;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TokenCounterSlave extends AMapReduceTracer implements SlaveInterface {
    private int threadId;
    private List<KeyValue<String, Integer>> keyValueList;
    private TokenCounterModel model;

    public TokenCounterSlave(int threadId, TokenCounterModel model) {
        this.threadId = threadId;
        this.model = model;
        this.keyValueList = new ArrayList<>();
    }

    public synchronized void notifySlave() {
        this.synchronizedNotify();
    }

    public void splitBoundedBuffer() {
        ArrayBlockingQueue<KeyValue<String, Integer>> boundedBuffer = this.model.getBoundedBuffer();
        try {
            while(true) {
                this.traceDequeueRequest(boundedBuffer);
                KeyValue<String, Integer> consumedItem = boundedBuffer.take();
                if (consumedItem.getKey() == null) {
                    break;
                }
                this.traceDequeue(consumedItem);
                this.keyValueList.add(consumedItem);
            }
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    public Map<String, Integer> reduceList(Reducer<String, Integer> reducer, List<KeyValue<String, Integer>> list) {
        return reducer.reduce(list);
    }

    private ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> splitReduction(Map<String, Integer> partiallyReducedMap) {
        ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> reduceQueueList = this.model.getReductionQueueList();
        TokenCounterPartitioner partitioner = TokenCounterPartitionerFactory.getPartitioner();
        for (Map.Entry<String, Integer> entry: partiallyReducedMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            int numOfPartitions = reduceQueueList.size();
            int index = partitioner.getPartition(key, value, numOfPartitions);
            this.tracePartitionAssigned(key, value, index, numOfPartitions);
            KeyValue<String, Integer> keyValue = new KeyValue<>(key, value);
            reduceQueueList.get(index).add(keyValue);
        }
        return reduceQueueList;
    }

    public void signalQuit() {
        this.traceQuit();
    }

    @Override
    public void run() {
        TokenCounterBarrier tokencounterBarrier = this.model.getBarrier();
        Reducer<String, Integer> reducer = TokenCounterReducerFactory.getReducer();
        while(true) {
            Map<String, Integer> originalMap = this.model.getResult();
            this.splitBoundedBuffer();
            Map<String, Integer> partiallyReducedMap = this.reduceList(reducer, this.keyValueList);
            ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> reductionQueueList = this.splitReduction(partiallyReducedMap);
            tokencounterBarrier.barrier();
            this.traceSplitAfterBarrier(this.threadId, reductionQueueList);
            for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue : reductionQueueList) {
                List<KeyValue<String, Integer>> keyValues = List.copyOf(reductionQueue);
                this.reduceList(reducer, keyValues);
            }
            Joiner joiner = this.model.getJoiner();
            joiner.finished();
            this.traceAddedToMap(originalMap, this.model.getResult());
            try {
                this.synchronizedWait();
            } catch (InterruptedException ex) {
                Tracer.error(ex.getMessage());
                break;
            }
        }
    }
}
