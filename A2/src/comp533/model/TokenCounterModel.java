package comp533.model;

import comp533.barrier.TokenCounterBarrier;
import comp533.joiner.Joiner;
import comp533.joiner.TokenCounterJoiner;
import comp533.keyvalue.KeyValue;
import comp533.mapper.Mapper;
import comp533.mapper.TokenCounterMapper;
import comp533.slave.TokenCounterSlave;
import comp533.view.TokenCounterView;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TokenCounterModel extends AMapReduceTracer {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private int numThreads;
    private List<Thread> threads;
    private List<TokenCounterSlave> slaves;
    private String inputString;
    private Map<String, Integer> result;
    private ArrayBlockingQueue<KeyValue<String, Integer>> boundedBuffer;
    private ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> reductionQueueList;
    private Joiner joiner;
    private TokenCounterBarrier barrier;

    public TokenCounterModel() {
        this.inputString = null;
        this.numThreads = 0;
        this.reductionQueueList = new ArrayList<>();
        this.slaves = new ArrayList<>();
        this.boundedBuffer = new ArrayBlockingQueue<>(this.BUFFER_SIZE);
    }

    public String getInputString() {
        return this.inputString;
    }

    public void clearReductionQueues() {
        for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue: reductionQueueList) {
            reductionQueue.clear();
        }
    }

    public void possiblyUnblockSlaveThreads() {
        for (int i = 0; i < this.numThreads; i++) {
            TokenCounterSlave currentSlave = this.slaves.get(i);
            currentSlave.notifySlave();
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
        this.result = new HashMap<>();
        this.boundedBuffer = new ArrayBlockingQueue<>(this.BUFFER_SIZE);
        for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue: this.reductionQueueList) {
            reductionQueue.clear();
        }
    }

    public void produceBoundedBuffer(KeyValue<String, Integer> keyValue) {
        try {
            this.traceEnqueueRequest(keyValue);
            this.boundedBuffer.put(keyValue);
            this.traceEnqueue(this.boundedBuffer);
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    public void endEnqueue() {
        try {
            for (int i = 0; i < this.numThreads; i++) {
                KeyValue<String, Integer> endKeyValue = new KeyValue<>(null, null);
                this.traceEnqueueRequest(endKeyValue);
                this.boundedBuffer.put(endKeyValue);
            }
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    public void startThreads() {
        for (Thread currentThread: this.threads) {
            if (currentThread.getState() == Thread.State.NEW) {
                currentThread.start();
            }
        }
    }

    public void problemSplit(String newInputString) {
        String[] tokens = newInputString.split(" ");
        Mapper<String, Integer> mapper = new TokenCounterMapper();
        for (String token: tokens) {
            KeyValue<String, Integer> keyValue = mapper.map(token);
            this.produceBoundedBuffer(keyValue);
        }
        this.endEnqueue();
    }

    public void mergeResults(TokenCounterView view) {
        Map<String, Integer> oldResult = this.result;
        this.result = new HashMap<>();
        for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue : this.reductionQueueList) {
            for (KeyValue<String, Integer> keyValues: reductionQueue) {
                String key = keyValues.getKey();
                Integer value = keyValues.getValue();
                this.result.put(key, value);
            }
        }
        PropertyChangeEvent updateResultEvent = new PropertyChangeEvent(this, "Result",
                oldResult, this.result);
        view.printNotification(updateResultEvent);
        this.pcs.firePropertyChange(updateResultEvent);
    }

    public void setInputString(String newInputString, TokenCounterView view) {
        this.updateInputString(newInputString, view);
        this.initializeStructures();
        this.clearReductionQueues();
        this.startThreads();
        this.possiblyUnblockSlaveThreads();
        this.problemSplit(newInputString);
        this.joiner.join();
        this.mergeResults(view);
    }

    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        this.pcs.addPropertyChangeListener(aListener);
    }

    public int getNumThreads() {
        return this.numThreads;
    }

    public TokenCounterBarrier getBarrier() {
        return this.barrier;
    }

    public ArrayBlockingQueue<KeyValue<String, Integer>> getBoundedBuffer() {
        return this.boundedBuffer;
    }

    public Joiner getJoiner() {
        return this.joiner;
    }

    public ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> getReductionQueueList() {
        return this.reductionQueueList;
    }

    public Map<String, Integer> getResult() {
        return this.result;
    }

    public void updateThreads(int numThreads, TokenCounterView view) {
        List<Thread> oldThreads = this.threads;
        this.joiner = new TokenCounterJoiner(numThreads);
        this.barrier = new TokenCounterBarrier(numThreads);
        this.threads = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            TokenCounterSlave slave = new TokenCounterSlave(i, this);
            this.slaves.add(slave);
            Thread newThread = new Thread(slave, "Slave" + i);
            this.threads.add(newThread);
            ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue = new ConcurrentLinkedQueue<>();
            this.reductionQueueList.add(reductionQueue);
        }
        PropertyChangeEvent updateThreadsEvent = new PropertyChangeEvent(this, "Threads",
                oldThreads, this.threads);
        view.printNotification(updateThreadsEvent);
        this.pcs.firePropertyChange(updateThreadsEvent);
    }

    public void interruptThreads() {
        for (int i = 0; i < this.numThreads; i++) {
            this.slaves.get(i).signalQuit();
            this.threads.get(i).interrupt();
        }
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
