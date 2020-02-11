package comp533.barrier;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

public class TokenCounterBarrier extends AMapReduceTracer implements Barrier {
    private int barrierCount;
    private int threadCtr;

    public TokenCounterBarrier(int aNumThreads) {
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
                this.traceBarrierReleaseAll(this.barrierCount, this.barrierCount, this.threadCtr);
            } else {
                this.traceBarrierWaitStart(this, this.barrierCount, this.threadCtr);
                this.wait();
                this.traceBarrierWaitEnd(this, this.barrierCount, this.threadCtr);
            }
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }
}
