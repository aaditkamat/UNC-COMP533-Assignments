package coupledsims.nio;


import java.nio.channels.SocketChannel;

public class ByteBufferSocketChannelInfo {
    ByteBufferInfo messageInfo;

    public ByteBufferSocketChannelInfo(ByteBufferInfo messageInfo, SocketChannel socketChannel) {
        this.messageInfo = messageInfo;
        this.socketChannel = socketChannel;
    }

    SocketChannel socketChannel;

    public ByteBufferInfo getMessageInfo() {
        return messageInfo;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

}
