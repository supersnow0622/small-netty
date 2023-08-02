package com.wlx.netty.client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * EchoClientHandler
 *
 * @author: liuzhiguo
 * @date: 2021/9/21 16:10
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("EchoClientHandler.channelActive");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        System.out.println("EchoClientHandler.channelRead:" + msg);
    }

}
