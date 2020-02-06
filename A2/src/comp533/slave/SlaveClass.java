package comp533.slave;

import comp533.model.TokenCounterModel;

public class SlaveClass implements SlaveInterface {
    private int threadId;
    private TokenCounterModel model;

    public SlaveClass(int threadId, TokenCounterModel model) {
        this.threadId = threadId;
        this.model = model;
    }

    public synchronized void notifySlave() {
        this.notify();
    }

    @Override
    public void run() {
    }
}
