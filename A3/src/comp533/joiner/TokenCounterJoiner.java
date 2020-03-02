package comp533.joiner;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

public class TokenCounterJoiner extends AMapReduceTracer implements Joiner {
    private int joinerCount;
    private int finishedCtr;

    public TokenCounterJoiner(int aNumThreads) {
        this.joinerCount = aNumThreads;
        this.finishedCtr = 0;
        this.traceJoinerCreated(this, aNumThreads);
    }

    public void finished() {
        this.finishedCtr += 1;
        this.traceJoinerFinishedTask(this, this.joinerCount, this.finishedCtr);
        if (this.finishedCtr == this.joinerCount) {
            this.synchronizedNotify();
        }
    }

    public synchronized void join() {
        try {
           this.traceJoinerWaitStart(this, this.joinerCount, this.finishedCtr);
           this.synchronizedWait();
           this.traceJoinerWaitEnd(this, this.joinerCount, this.finishedCtr);
           this.finishedCtr = 0;
        } catch (InterruptedException ex){
            Tracer.error(ex.getMessage());
        }

    }
}
