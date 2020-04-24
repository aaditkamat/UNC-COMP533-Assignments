package coupledsims.client;

import coupledsims.nio.ByteBufferInfo;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class ClientRunnable implements Runnable {
    NIOClient client;

    public ClientRunnable(NIOClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            ArrayBlockingQueue<ByteBufferInfo> messageQueue = this.client.getMessageQueue();
            ByteBufferInfo messageInfo = messageQueue.take();
            ByteBuffer message = messageInfo.getMessage();
            int messageLength = messageInfo.getMessageLength();
            this.client.receiveProposalLearnedNotificationViaNIO(message, messageLength);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
