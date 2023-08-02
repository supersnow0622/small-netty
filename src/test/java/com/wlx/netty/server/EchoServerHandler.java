package com.wlx.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
//        System.out.println("EchoServer active");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        System.out.println("EchoServer read：" + msg);
        channelHandlerContext.writeAndFlush("hello");
    }
}
