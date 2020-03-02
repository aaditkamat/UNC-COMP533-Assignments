package comp533.mvc;

import comp533.barrier.Barrier;
import comp533.client.Client;
import comp533.joiner.Joiner;
import comp533.keyvalue.KeyValue;
import comp533.view.View;
import util.models.PropertyListenerRegisterer;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface TokenCounter extends PropertyListenerRegisterer {
    String COUNTER_NAME = "Counter";
    Map<String, Integer> getResult();
    ArrayBlockingQueue<KeyValue<String, Integer>> getBoundedBuffer();
    ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> getReductionQueueList();
    Barrier getBarrier();
    Joiner getJoiner();
    void interruptThreads();
    void setInputString(String newInputString, View view);
    void updateThreads();
    void setNumThreads(int numThreads, View view);
}
