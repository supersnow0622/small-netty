package io.netty.channel;

import java.nio.channels.SelectableChannel;

public abstract class AbstractChannelHandlerContext implements ChannelHandlerContext {

    private final DefaultChannelPipeline pipeline;
    volatile AbstractChannelHandlerContext prev;

    volatile AbstractChannelHandlerContext next;

    public AbstractChannelHandlerContext(DefaultChannelPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public SelectableChannel channel() {
        return pipeline.channel();
    }

    @Override
    public DefaultChannelPipeline pipeline() {
        return pipeline;
    }

    static void invokeChannelActive(AbstractChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.invokeChannelActive();
    }

    private void invokeChannelActive() {
        ((ChannelInboundHandler)handler()).channelActive(this);
    }

    // 事件往后传递，需要拦截active操作的handler需要实现此方法
    @Override
    public ChannelHandlerContext fireChannelActive() {
        next.invokeChannelActive();
        return this;
    }

    static void invokeChannelRead(AbstractChannelHandlerContext channelHandlerContext, Object msg) {
        channelHandlerContext.invokeChannelRead(msg);
    }

    private void invokeChannelRead(Object msg) {
        ((ChannelInboundHandler)handler()).channelRead(this, msg);
    }

    // 事件往后传递，需要拦截read操作的handler需要实现此方法
    @Override
    public void fireChannelRead(Object obj) {
        next.invokeChannelRead(obj);
    }

}
