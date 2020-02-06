package comp533.joiner;

import util.trace.Tracer;

public class Joiner implements JoinerInterface {
    private int joinerCount;
    private int finishedCtr;

    public Joiner(int aNumThreads) {
        this.joinerCount = aNumThreads;
        this.finishedCtr = 0;
    }

    public void finished() {
        this.finishedCtr += 1;
    }

    public synchronized void join() {
        try {
            if (this.finishedCtr == this.joinerCount) {
                notify();
                this.finishedCtr = 0;
            } else {
                this.wait();
            }
        } catch (InterruptedException ex){
            Tracer.error(ex.getMessage());
        }

    }
}
