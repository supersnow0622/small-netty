package io.netty.channel.socket.nio;

import io.netty.channel.nio.AbstractNioChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class NioSocketChannel extends AbstractNioChannel {

    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();

    public NioSocketChannel() {
        this(newSocket(DEFAULT_SELECTOR_PROVIDER));
    }

    public NioSocketChannel(NioServerSocketChannel parent, SocketChannel socketChannel) {
        super(parent, socketChannel, SelectionKey.OP_READ);
    }

    public NioSocketChannel(SocketChannel socketChannel) {
        super(null, socketChannel, SelectionKey.OP_READ);
    }

    private static SocketChannel newSocket(SelectorProvider selectorProvider) {
        try {
            SocketChannel socketChannel = selectorProvider.openSocketChannel();
            socketChannel.configureBlocking(false);
            return socketChannel;
        } catch (IOException e) {
            throw new RuntimeException("Failed to open a socket", e);
        }
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return new SocketChannelUnsafe();
    }

    @Override
    public SocketChannel javaChannel() {
        return (SocketChannel) channel;
    }

    @Override
    public void doBind(InetSocketAddress socketAddress) throws Exception {
    }

    private final class SocketChannelUnsafe extends AbstractUnsafe {

        @Override
        public void read() {
            try {
                String msg = readMessage();
                if (msg == null) {
                    return;
                }

                pipeline().fireChannelRead(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private String readMessage() throws IOException {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            SocketChannel socketChannel = javaChannel();

            int readByteSize = 0;
            try {
                readByteSize = socketChannel.read(byteBuffer);
            } catch (IOException e) {
                selectionKey.cancel();
                socketChannel.close();
                System.out.println("链路断开");
                return null;
            }

            if (readByteSize > 0) {
                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                return new String(bytes, "UTF-8");
            }

            return null;
        }
    }
}
