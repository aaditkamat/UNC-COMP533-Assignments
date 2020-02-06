package comp533.barrier;

import util.trace.Tracer;

public class Barrier implements BarrierInterface {
    private int barrierCount;
    private int threadCtr;

    public Barrier(int aNumThreads) {
        this.barrierCount = aNumThreads;
        this.threadCtr = 0;
    }

    public synchronized void barrier() {
        this.threadCtr += 1;
        try {
            if (this.threadCtr == this.barrierCount) {
                this.notifyAll();
                this.threadCtr = 0;
            } else {
                this.wait();
            }
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }
}
