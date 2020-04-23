package coupledsims.client;

import inputport.nio.manager.NIOManager;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import util.trace.port.nio.SocketChannelRead;
import util.trace.port.nio.SocketChannelWritten;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

public class ClientRunnable implements Runnable {
    NIOClient client;
    ByteBuffer message;

    public ClientRunnable(NIOClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            ArrayBlockingQueue<ByteBufferInfo> messageQueue = this.client.getMessageQueue();
            ByteBufferInfo messageInfo = messageQueue.take();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
