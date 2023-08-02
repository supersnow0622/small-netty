package com.wlx.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class MyServerChannelInitializer extends ChannelInitializer {
    @Override
    public void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new EchoServerHandler());
    }

}
