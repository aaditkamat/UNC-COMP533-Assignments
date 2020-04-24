package coupledsims.server;

import coupledsims.nio.ByteBufferSocketChannelInfo;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import inputport.nio.manager.listeners.SocketChannelReadListener;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

public interface NIOServer extends SocketChannelAcceptListener, SocketChannelReadListener {
    String READ_THREAD_NAME = "Read Thread";
    NIOManager getNioManager();
    ArrayBlockingQueue<ByteBufferSocketChannelInfo> getMessageQueue();
    void sendRequestViaNIO(String command, SocketChannel socketChannel);
}
