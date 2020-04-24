package coupledsims.client;

import coupledsims.nio.ByteBufferInfo;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.listeners.SocketChannelConnectListener;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

public interface NIOClient extends SocketChannelConnectListener {
    String READ_THREAD_NAME = "Read Thread";
    int BUFFER_SIZE = 10;
    NIOManager getNIOManager();
    SocketChannel getSocketChannel();
    ArrayBlockingQueue<ByteBufferInfo> getMessageQueue();
    void receiveProposalLearnedNotificationViaNIO(ByteBuffer message, int messageLength);
}
