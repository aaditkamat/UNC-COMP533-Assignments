package comp533.barrier;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

public class Barrier extends AMapReduceTracer implements BarrierInterface {
    private int barrierCount;
    private int threadCtr;

    public Barrier(int aNumThreads) {
        this.barrierCount = aNumThreads;
        this.threadCtr = 0;
        this.traceBarrierCreated(this, aNumThreads);
    }

    public synchronized void barrier() {
        this.threadCtr += 1;
        try {
            if (this.threadCtr == this.barrierCount) {
                this.notifyAll();
                this.threadCtr = 0;
            } else {
                this.traceWait();
                this.wait();
            }
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }
}
