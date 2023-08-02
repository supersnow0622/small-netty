package io.netty.channel;

public class ChannelInboundHandlerAdapter implements ChannelInboundHandler {
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        channelHandlerContext.fireChannelRead(msg);
    }
}
