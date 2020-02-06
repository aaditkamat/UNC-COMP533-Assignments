package comp533.model;

import comp533.barrier.Barrier;
import comp533.joiner.JoinerInterface;
import comp533.keyvalue.KeyValue;
import comp533.mapper.Mapper;
import comp533.mapper.TokenCountingMapper;
import comp533.reducer.TokenCountingReducer;
import comp533.slave.SlaveClass;
import comp533.view.TokenCounterView;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class TokenCounterModel extends AMapReduceTracer {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private int numThreads;
    private List<Thread> threads;
    private String inputString;
    private Map<String, Integer> result;
    private ArrayBlockingQueue<KeyValue<String, Integer>> keyValueQueue;
    private ArrayList<LinkedList<KeyValue<String, Integer>>> reductionQueueList;
    private JoinerInterface joiner;
    private Barrier barrier;

    public TokenCounterModel() {
        this.inputString = null;
        this.numThreads = 0;
    }

    public String getInputString() {
        return this.inputString;
    }

    public void clearReductionQueues() {
        for (LinkedList<KeyValue<String, Integer>> reductionQueue: reductionQueueList) {
            reductionQueue.clear();
        }
    }

    public void possiblyUnblockSlaveThreads() {
        for (Thread thread: threads) {
            if (thread.getState() == Thread.State.WAITING) {
                thread.notify();
            }
        }
    }

    public void updateInputString(String newInputString, TokenCounterView view) {
        String oldInputString = this.inputString;
        this.inputString = newInputString;
        PropertyChangeEvent updateInputStringEvent = new PropertyChangeEvent(this, "InputString",
                oldInputString, newInputString);
        view.printNotification(updateInputStringEvent);
        this.pcs.firePropertyChange(updateInputStringEvent);
    }

    public void initializeStructures() {
        this.result = new HashMap<String, Integer>();
        this.keyValueQueue = new ArrayBlockingQueue<KeyValue<String, Integer>>(this.BUFFER_SIZE);
        this.reductionQueueList = new ArrayList<>();
    }

    public void updateResult(TokenCounterView view) {
        Map<String, Integer> oldResult = this.result;
        PropertyChangeEvent updateResultEvent = new PropertyChangeEvent(this, "Result",
                oldResult, this.result);
        view.printNotification(updateResultEvent);
        this.pcs.firePropertyChange(updateResultEvent);
    }

    public void produceBoundedBuffer(KeyValue<String, Integer> keyValue) {
        try {
            this.traceEnqueueRequest(keyValue);
            this.keyValueQueue.put(keyValue);
            this.traceEnqueue(keyValue);
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    public void endEnqueue() {
        try {
            KeyValue<String, Integer> endKeyValue = new KeyValue<>(null, null);
            this.traceEnqueueRequest(endKeyValue);
            this.keyValueQueue.put(endKeyValue);
            this.traceEnqueue(endKeyValue);
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    public void problemSplit(String newInputString) {
        String[] tokens = newInputString.split(" ");
        Mapper<String, Integer> mapper = new TokenCountingMapper();
        for (String token: tokens) {
            KeyValue<String, Integer> keyValue = mapper.map(token);
            this.produceBoundedBuffer(keyValue);
        }
        this.endEnqueue();
    }

    public void setInputString(String newInputString, TokenCounterView view) {
        this.updateInputString(newInputString, view);
        this.initializeStructures();
        this.clearReductionQueues();
        this.possiblyUnblockSlaveThreads();
        this.problemSplit(newInputString);
        this.joiner.join();
        this.updateResult(view);
    }

    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        this.pcs.addPropertyChangeListener(aListener);
    }

    public int getNumThreads() {
        return this.numThreads;
    }

    public Barrier getBarrier() {
        return this.barrier;
    }

    public ArrayBlockingQueue<KeyValue<String, Integer>> getKeyValueQueue() {
        return this.keyValueQueue;
    }

    public JoinerInterface getJoiner() {
        return this.joiner;
    }

    public ArrayList<LinkedList<KeyValue<String, Integer>>> getReductionQueueList() {
        return this.reductionQueueList;
    }

    public Map<String, Integer> getResult() {
        return this.result;
    }

    public void updateThreads(int numThreads, TokenCounterView view) {
        List<Thread> oldThreads = this.threads;
        this.threads = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            SlaveClass slave = new SlaveClass(i, this);
            this.threads.add(new Thread(slave, "Slave" + i));
            LinkedList<KeyValue<String, Integer>> reductionQueue = new LinkedList<>();
            this.reductionQueueList.add(reductionQueue);
        }
        PropertyChangeEvent updateThreadsEvent = new PropertyChangeEvent(this, "Threads",
                oldThreads, this.threads);
        view.printNotification(updateThreadsEvent);
        this.pcs.firePropertyChange(updateThreadsEvent);
    }

    public void setNumThreads(int numThreads, TokenCounterView view) {
        int oldNumThreads = this.numThreads;
        this.numThreads = numThreads;
        PropertyChangeEvent updateInputStringEvent = new PropertyChangeEvent(this, "NumThreads",
                oldNumThreads, numThreads);
        view.printNotification(updateInputStringEvent);
        this.pcs.firePropertyChange(updateInputStringEvent);
        this.updateThreads(numThreads, view);
    }


    @Override
    public String toString() {
        return AMapReduceTracer.MODEL;
    }
}
