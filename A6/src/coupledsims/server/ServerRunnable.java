package coupledsims.server;

import coupledsims.nio.ByteBufferSocketChannelInfo;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;


import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ServerRunnable extends AMapReduceTracer implements Runnable {
    NIOServer server;

    public ServerRunnable(NIOServer server) {
        this.server = server;
    }

    public void notifyRunnable() {
        this.synchronizedNotify();
    }

    @Override
    public synchronized void run() {
        do {
            try {
                ByteBufferSocketChannelInfo messageAndChannelInfo = this.server.getMessageQueue().take();
                ByteBuffer message = messageAndChannelInfo.getMessageInfo().getMessage();
                int messageLength = messageAndChannelInfo.getMessageInfo().getMessageLength();
                SocketChannel socketChannel = messageAndChannelInfo.getSocketChannel();
                String command = new String(message.array(), message.position(), messageLength);
                this.server.sendRequestViaNIO(command, socketChannel);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                break;
            }
        } while(true);
    }

}
