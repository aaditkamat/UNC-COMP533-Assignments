package comp533.joiner;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

public class Joiner extends AMapReduceTracer implements JoinerInterface {
    private int joinerCount;
    private int finishedCtr;

    public Joiner(int aNumThreads) {
        this.joinerCount = aNumThreads;
        this.finishedCtr = 0;
        this.traceJoinerCreated(this, aNumThreads);
    }

    public void finished() {
        this.finishedCtr += 1;
    }

    public synchronized void join() {
        try {
            if (this.finishedCtr == this.joinerCount) {
                this.synchronizedNotify();
                this.finishedCtr = 0;
            } else {
                this.synchronizedWait();
            }
        } catch (InterruptedException ex){
            Tracer.error(ex.getMessage());
        }

    }
}
