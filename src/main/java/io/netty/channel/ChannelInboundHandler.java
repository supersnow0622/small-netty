package io.netty.channel;

public interface ChannelInboundHandler extends ChannelHandler {

    void channelActive(ChannelHandlerContext channelHandlerContext);

    void channelRead(ChannelHandlerContext channelHandlerContext, Object msg);
}
