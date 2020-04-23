package coupledsims.client;

import java.nio.ByteBuffer;

public class ByteBufferInfo {
    ByteBuffer message;
    Integer MessageLength;

    public ByteBufferInfo(ByteBuffer message, Integer messageLength) {
        this.message = message;
        MessageLength = messageLength;
    }
}
