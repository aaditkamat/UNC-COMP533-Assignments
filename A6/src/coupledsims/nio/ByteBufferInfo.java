package coupledsims.nio;

import java.nio.ByteBuffer;

public class ByteBufferInfo {
    private ByteBuffer message;
    private Integer messageLength;

    public ByteBufferInfo(ByteBuffer message, Integer messageLength) {
        this.message = message;
        this.messageLength = messageLength;
    }

    public ByteBuffer getMessage() {
        return message;
    }

    public Integer getMessageLength() {
        return messageLength;
    }
}
