package coupledsims.client;

import coupledsims.nio.ByteBufferInfo;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class ClientRunnable extends AMapReduceTracer implements Runnable {
    NIOClient client;

    public ClientRunnable(NIOClient client) {
        this.client = client;
    }

    public void notifyRunnable() {
        this.synchronizedNotify();
    }

    @Override
    public void run() {
        do {
            try {
                ByteBufferInfo messageInfo = this.client.getMessageQueue().take();
                ByteBuffer message = messageInfo.getMessage();
                int messageLength = messageInfo.getMessageLength();
                String messageString = new String(message.array(), message.position(), messageLength);
                this.client.receiveProposalLearnedNotificationViaNIO(messageString);
                this.synchronizedWait();
            } catch (NullPointerException | InterruptedException ex) {
                ex.printStackTrace();
                break;
            }
        } while (true);
    }
}
