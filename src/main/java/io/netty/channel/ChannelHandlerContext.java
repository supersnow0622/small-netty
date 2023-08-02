package io.netty.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

public interface ChannelHandlerContext extends ChannelHandler {

    ChannelHandler handler();

    ChannelHandlerContext fireChannelActive();

    void fireChannelRead(Object obj);

    SelectableChannel channel();

    DefaultChannelPipeline pipeline();

    default void writeAndFlush(String msg) {
        if (msg != null && msg.trim().length() > 0) {
            byte[] bytes = msg.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();

            try {
                ((SocketChannel)channel()).write(byteBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
