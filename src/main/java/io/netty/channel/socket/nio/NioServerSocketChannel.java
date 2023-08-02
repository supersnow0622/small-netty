package io.netty.channel.socket.nio;

import io.netty.channel.nio.AbstractNioChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;

public class NioServerSocketChannel extends AbstractNioChannel {

    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();

    public NioServerSocketChannel() {
        this(newSocket(DEFAULT_SELECTOR_PROVIDER));
    }

    public NioServerSocketChannel(ServerSocketChannel serverSocketChannel) {
        super(serverSocketChannel, SelectionKey.OP_ACCEPT);
    }

    private static ServerSocketChannel newSocket(SelectorProvider selectorProvider) {
        try {
            ServerSocketChannel serverSocketChannel = selectorProvider.openServerSocketChannel();
            serverSocketChannel.configureBlocking(false);
            return serverSocketChannel;
        } catch (Exception e) {
            throw new RuntimeException("Failed to open a server socket.", e);
        }
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return new MessageUnsafe();
    }

    @Override
    public ServerSocketChannel javaChannel() {
        return (ServerSocketChannel) channel;
    }

    @Override
    public void doBind(InetSocketAddress socketAddress) throws Exception {
        javaChannel().bind(socketAddress);
        System.out.println("服务端启动成功，端口：" + socketAddress.getPort());
    }



    protected void doReadMessage(List<Object> readBuf) throws IOException {
        SocketChannel socketChannel = javaChannel().accept();
        if (socketChannel != null) {
            readBuf.add(new NioSocketChannel(this, socketChannel));
        } else {
            socketChannel.close();
        }
    }

    private final class MessageUnsafe extends AbstractUnsafe {

        private final List<Object> readBuf = new ArrayList<>();

        @Override
        public void read() throws IOException {
            doReadMessage(readBuf);

            for (Object object : readBuf) {
                pipeline().fireChannelRead(object);
            }
        }
    }
}
