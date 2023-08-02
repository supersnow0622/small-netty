package io.netty.channel;

import java.nio.channels.SelectableChannel;

/**
 * DefaultChannelPipeline内部双向链表，是ChannelHandler的容器，负责Handler的管理和事件拦截与调度；
 * ChannelPipeline是线程安全的，Netty中采用synchronized同步块来实现线程安全；
 * ChannelHandler是线程非安全的；
 * 当发生某个IO事件时，都会在pipeline中传播和处理，它是事件处理的总入口；
 */
public class DefaultChannelPipeline implements ChannelPipeline {

    private AbstractChannelHandlerContext head;

    private AbstractChannelHandlerContext tail;

    private Channel channel;

    public DefaultChannelPipeline(Channel channel) {
        this.channel = channel;
        this.head = new HeadContext(this);
        this.tail = new TailContext(this);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    public ChannelPipeline addLast(ChannelHandler... channelHandlers) {
        for (ChannelHandler channelHandler : channelHandlers) {
            if (channelHandler == null) {
                break;
            }

            //添加Handler
            addLast(null, channelHandler);
        }
        return this;
    }

    public final ChannelPipeline addLast(String name, ChannelHandler handler) {
        final AbstractChannelHandlerContext newCtx;
        synchronized (this) {
            //将handler封装成DefaultChannelHandlerContext放入pipeline
            newCtx = newContext(handler);
            //将封装后的handler追加到pipeline的后面（tail之前）
            addLast0(newCtx);
        }

        return this;
    }

    private AbstractChannelHandlerContext newContext(ChannelHandler channelHandler) {
        return new DefaultChannelHandlerContext(this, channelHandler);
    }

    // 尾插法插入节点
    private void addLast0(AbstractChannelHandlerContext context) {
        AbstractChannelHandlerContext prev = this.tail.prev;
        context.next = this.tail;
        context.prev = prev;
        prev.next = context;
        this.tail.prev = context;
    }

    public final ChannelPipeline fireChannelActive() {
        AbstractChannelHandlerContext.invokeChannelActive(head);
        return this;
    }

    public final ChannelPipeline fireChannelRead(Object msg) {
        AbstractChannelHandlerContext.invokeChannelRead(head, msg);
        return this;
    }

    public SelectableChannel channel() {
        return channel.javaChannel();
    }

    class HeadContext extends AbstractChannelHandlerContext implements ChannelInboundHandler {

        public HeadContext(DefaultChannelPipeline pipeline) {
            super(pipeline);
        }

        @Override
        public ChannelHandler handler() {
            return this;
        }

        @Override
        public void channelActive(ChannelHandlerContext channelHandlerContext) {
            channelHandlerContext.fireChannelActive();
        }

        @Override
        public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
            channelHandlerContext.fireChannelRead(msg);
        }
    }

    class TailContext extends AbstractChannelHandlerContext implements ChannelInboundHandler {

        public TailContext(DefaultChannelPipeline pipeline) {
            super(pipeline);
        }

        @Override
        public ChannelHandler handler() {
            return this;
        }

        @Override
        public void channelActive(ChannelHandlerContext channelHandlerContext) {
        }

        @Override
        public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        }
    }
}
