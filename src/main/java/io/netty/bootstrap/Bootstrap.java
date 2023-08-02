package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Future;

public class Bootstrap extends AbstractBootstrap {

    @Override
    void init(Channel channel) throws Exception {
        ((ChannelInitializer)getHandler()).initChannel(channel);
    }

    public void connect(String address, int port) {
        ChannelFuture channelFuture = initAndRegister();
        doConnect(channelFuture, address, port);
    }

    private void doConnect(ChannelFuture channelFuture, String address, int port) {
        Channel channel = channelFuture.channel();
        if (channelFuture.isDone()) {
            doConnect0(channel, address, port);
        } else {
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    doConnect0(channel, address, port);
                }
            });
        }
    }

    private void doConnect0(Channel channel, String address, int port) {
        try {
            boolean connected = ((SocketChannel) channel.javaChannel()).connect(new InetSocketAddress(address, port));
            //此时链接很可能还未成功，在selector上注册OP_CONNECT，在NioEventLoop的selector.select()事件发生时修改socketChannel为OP_READ
            if (!connected) {
                channel.getSelectionKey().interestOps(SelectionKey.OP_CONNECT);
                System.out.println("客户端连接远端：" + address + ":" + port + "未成功");
            } else {
                System.out.println("客户端连接远端：" + address + ":" + port + "已成功");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
