package comp533.mvc;

import comp533.barrier.Barrier;
import comp533.barrier.TokenCounterBarrier;
import comp533.client.Client;
import comp533.joiner.Joiner;
import comp533.joiner.TokenCounterJoiner;
import comp533.keyvalue.KeyValue;
import comp533.keyvalue.TokenCounterKeyValue;
import comp533.mapper.Mapper;
import comp533.mapper.TokenCounterMapper;
import comp533.slave.Slave;
import comp533.slave.TokenCounterSlave;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DistributedTokenCounter extends AMapReduceTracer implements TokenCounter, RemoteTokenCounter {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Map<String, Integer> result;
    private int id;
    private int numThreads;
    private List<Thread> threads;
    private List<Slave> slaves;
    private String inputString;
    private ArrayBlockingQueue<KeyValue<String, Integer>> boundedBuffer;
    private ArrayList<ConcurrentLinkedQueue<KeyValue<String, Integer>>> reductionQueueList;
    private Joiner joiner;
    private Barrier barrier;
    private List<Client> registeredClients;
    private Stack<Slave> unassignedSlaves;
    private Stack<Client> unassignedClients;


    public DistributedTokenCounter(int id) {
        this.inputString = null;
        this.numThreads = 0;
        this.reductionQueueList = new ArrayList<>();
        this.slaves = new ArrayList<>();
        this.boundedBuffer = new ArrayBlockingQueue<>(this.BUFFER_SIZE);
        this.registeredClients = new ArrayList<>();
        this.unassignedSlaves = new Stack<>();
        this.unassignedClients = new Stack<>();
        this.id = id;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    protected void processPropertyChangeEvent(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }

    public String getInputString() {
        return this.inputString;
    }

    public void matchSlavesToClients() {
        if (!this.unassignedSlaves.isEmpty() && !this.unassignedClients.isEmpty()) {
            Slave unassignedSlave = this.unassignedSlaves.peek();
            Client unassignedClient = this.unassignedClients.peek();
            unassignedSlave.setClient(unassignedClient);
        }
    }

    private void clearReductionQueues() {
        for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue: reductionQueueList) {
            reductionQueue.clear();
        }
    }

    private void possiblyUnblockSlaveThreads() {
        for (int i = 0; i < this.numThreads; i++) {
            Slave currentSlave = this.slaves.get(i);
            currentSlave.notifySlave();
        }
    }

    private void initializeStructures() {
        this.result = new HashMap<>();
        this.boundedBuffer = new ArrayBlockingQueue<>(this.BUFFER_SIZE);
        for (ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue: this.reductionQueueList) {
            reductionQueue.clear();
        }
    }

    private void produceBoundedBuffer(KeyValue<String, Integer> keyValue) {
        try {
            this.traceEnqueueRequest(keyValue);
            this.boundedBuffer.put(keyValue);
            this.traceEnqueue(this.boundedBuffer);
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    private void endEnqueue() {
        try {
            for (int i = 0; i < this.numThreads; i++) {
                KeyValue<String, Integer> endKeyValue = new TokenCounterKeyValue<>(null, null);
                this.traceEnqueueRequest(endKeyValue);
                this.boundedBuffer.put(endKeyValue);
            }
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }

    private void startThreads() {
        for (Thread currentThread: this.threads) {
            if (currentThread.getState() == Thread.State.NEW) {
                currentThread.start();
            }
        }
    }

    private void problemSplit() {
        String[] tokens = this.inputString.split(" ");
        Mapper<String, Integer> mapper = new TokenCounterMapper();
        for (String token: tokens) {
            KeyValue<String, Integer> keyValue = mapper.map(token);
            this.produceBoundedBuffer(keyValue);
        }
        this.endEnqueue();
    }

    private void mergeIntermediaryResults() {
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
        this.pcs.firePropertyChange(updateResultEvent);
    }

    public void updateResult() {
        this.initializeStructures();
        this.clearReductionQueues();
        this.startThreads();
        this.possiblyUnblockSlaveThreads();
        this.problemSplit();
        this.joiner.join();
        this.mergeIntermediaryResults();
    }

    public void setInputString(String newInputString) {
        String oldInputString = this.inputString;
        this.inputString = newInputString;
        PropertyChangeEvent updateInputStringEvent = new PropertyChangeEvent(this, "InputString",
                oldInputString, newInputString);
        this.pcs.firePropertyChange(updateInputStringEvent);
        this.updateResult();
    }

    public int getNumThreads() {
        return this.numThreads;
    }

    public Barrier getBarrier() {
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

    public int getId() {
        return this.id;
    }

    public void updateThreads() {
        List<Thread> oldThreads = this.threads;
        this.joiner = new TokenCounterJoiner(this.numThreads);
        this.barrier = new TokenCounterBarrier(this.numThreads);
        this.threads = new ArrayList<>(this.numThreads);
        for (int i = 0; i < this.numThreads; i++) {
            TokenCounterSlave slave = new TokenCounterSlave(i, this);
            this.slaves.add(slave);
            this.unassignedSlaves.add(slave);
            Thread newThread = new Thread(slave, "Slave" + i);
            this.threads.add(newThread);
            ConcurrentLinkedQueue<KeyValue<String, Integer>> reductionQueue = new ConcurrentLinkedQueue<>();
            this.reductionQueueList.add(reductionQueue);
        }
        this.matchSlavesToClients();
        PropertyChangeEvent updateThreadsEvent = new PropertyChangeEvent(this, "Threads",
                oldThreads, this.threads);
        this.pcs.firePropertyChange(updateThreadsEvent);
    }

    public void quit() {
        for (Client registeredClient: registeredClients) {
            registeredClient.quit();
        }
    }

    public void registerClient(Client clientToRegister) throws RemoteException {
        this.traceRegister(clientToRegister);
        this.unassignedClients.add(clientToRegister);
        this.registeredClients.add(clientToRegister);
    }

    public void interruptThreads() {
        for (int i = 0; i < this.numThreads; i++) {
            this.slaves.get(i).signalQuit();
            this.threads.get(i).interrupt();
        }
    }

    public void setNumThreads(int numThreads) {
        int oldNumThreads = this.numThreads;
        this.numThreads = numThreads;
        PropertyChangeEvent setNumThreadsEvent = new PropertyChangeEvent(this, "NumThreads",
                oldNumThreads, numThreads);
        this.pcs.firePropertyChange(setNumThreadsEvent);
        this.updateThreads();
    }

    @Override
    public String toString() {
        return AMapReduceTracer.MODEL;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DistributedTokenCounter)) {
            return false;
        }
        DistributedTokenCounter otherCounter = (DistributedTokenCounter) obj;
        return this.getResult() == otherCounter.getResult();
    }
}
