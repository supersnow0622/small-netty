package com.wlx.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(1,"client");

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new MyClientChannelInitializer())
                ;
        bootstrap.connect("127.0.0.1", 8081);

    }
}
