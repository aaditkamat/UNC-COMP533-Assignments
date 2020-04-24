package coupledsims.server;

import coupledsims.nio.ByteBufferSocketChannelInfo;


import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

public class ServerRunnable implements Runnable {
    NIOServer server;

    public ServerRunnable(NIOServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            ArrayBlockingQueue<ByteBufferSocketChannelInfo> messageQueue = this.server.getMessageQueue();
            ByteBufferSocketChannelInfo messageAndChannelInfo = messageQueue.take();
            this.wait();
            ByteBuffer message = messageAndChannelInfo.getMessageInfo().getMessage();
            int messageLength = messageAndChannelInfo.getMessageInfo().getMessageLength();
            SocketChannel socketChannel = messageAndChannelInfo.getSocketChannel();
            String command = new String(message.array(), message.position(), messageLength);
            this.server.sendRequestViaNIO(command, socketChannel);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
