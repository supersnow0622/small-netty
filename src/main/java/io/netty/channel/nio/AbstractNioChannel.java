package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class AbstractNioChannel implements Channel {

    /**
     * NioServerSocketChannel实例
     */
    private Channel parent;
    /**
     * 此处是真正的socketChannel，java nio socket
     */
    protected SelectableChannel channel;
    /**
     * SelectionKey类型
     */
    protected int ops;

    protected SelectionKey selectionKey;

    private final Unsafe unsafe;

    private final DefaultChannelPipeline pipeline;
    private volatile EventLoop eventLoop;
    public AbstractNioChannel(Channel parent, SelectableChannel channel, int ops) {
        this.parent = parent;
        this.unsafe = newUnsafe();
        this.pipeline = new DefaultChannelPipeline(this);

        this.channel = channel;
        this.ops = ops;
        try {
            this.channel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AbstractNioChannel(SelectableChannel channel, int ops) {
        this.channel = channel;
        this.ops = ops;
        this.unsafe = newUnsafe();
        this.pipeline = new DefaultChannelPipeline(this);
    }

    protected abstract AbstractUnsafe newUnsafe();

    @Override
    public Unsafe unsafe() {
        return this.unsafe;
    }

    public DefaultChannelPipeline pipeline() {
        return pipeline;
    }

    public EventLoop eventLoop() {
        return eventLoop;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void register0(ChannelPromise promise) {
        try {
            this.selectionKey = javaChannel().register(eventLoop().selector(), ops, this);
            promise.setDone(true);
            promise.notifyListener();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract class AbstractUnsafe implements Unsafe {

        @Override
        public void register(EventLoop eventLoop, final ChannelPromise promise) {
            AbstractNioChannel.this.eventLoop = eventLoop;

            //首先判断当前线程是否channel对应的NioEventLoop线程，如果是同一线程，直接注册
            if (eventLoop.inEventLoop()) {
                register0(promise);
            } else {
                //如果是其他线程发起的注册操作，需要封装成Runnable任务，放到NioEventLoop线程的任务队列中执行
                eventLoop.execute(() -> register0(promise));
            }
        }
    }
}
