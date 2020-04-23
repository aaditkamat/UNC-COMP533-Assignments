package coupledsims.server;

import inputport.nio.manager.NIOManager;
import inputport.nio.manager.listeners.SocketChannelAcceptListener;

public interface NIOServer extends SocketChannelAcceptListener {
    String READ_THREAD_NAME = "Read Thread";
    NIOManager getNioManager();
}
