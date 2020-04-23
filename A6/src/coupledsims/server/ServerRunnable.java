package coupledsims.server;

import inputport.nio.manager.NIOManager;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import inputport.nio.manager.listeners.SocketChannelWriteListener;
import util.trace.port.nio.SocketChannelRead;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ServerRunnable implements Runnable, SocketChannelReadListener {
    NIOServer server;

    public ServerRunnable(NIOServer server) {
        this.server = server;
    }

    @Override
    public void socketChannelRead(SocketChannel socketChannel, ByteBuffer newMessage, int messageLength) {
        SocketChannelRead.newCase(this, socketChannel, newMessage, messageLength);
        NIOManager nioManager = this.server.getNioManager();
        nioManager.write(socketChannel, newMessage);
    }

    @Override
    public void run() {

    }
}
