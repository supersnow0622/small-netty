package io.netty.channel;

public class DefaultChannelHandlerContext extends AbstractChannelHandlerContext implements ChannelInboundHandler{

    private ChannelHandler childHandler;

    public DefaultChannelHandlerContext(DefaultChannelPipeline pipeline, ChannelHandler childHandler) {
        super(pipeline);
        this.childHandler = childHandler;
    }

    @Override
    public ChannelHandler handler() {
        return childHandler;
    }

    public void channelActive(ChannelHandlerContext channelHandlerContext) {
//        channelHandlerContext.fireChannelActive();
    }

    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
//        channelHandlerContext.fireChannelRead(msg);
    }
}
