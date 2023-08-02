package com.wlx.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;

/**
 * ClientChannelInitializer
 *
 * @author: liuzhiguo
 * @date: 2021/9/21 16:10
 */
public class MyClientChannelInitializer extends ChannelInitializer {

    @Override
    public void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new EchoClientHandler());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }
}
