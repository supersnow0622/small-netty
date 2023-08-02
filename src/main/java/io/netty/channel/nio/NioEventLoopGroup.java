package io.netty.channel.nio;

import io.netty.channel.*;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ThreadPerTaskExecutor;

import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public class NioEventLoopGroup implements EventLoopGroup {

    // 计算下一个执行器的编号
    private final AtomicLong idx = new AtomicLong();

    private static final int DEFAULT_EVENT_LOOP_THREADS = Runtime.getRuntime().availableProcessors() * 2;

    private EventExecutor[] children;

    public NioEventLoopGroup(String poolName) {
        this(DEFAULT_EVENT_LOOP_THREADS, poolName);
    }

    public NioEventLoopGroup(int nThreads, String poolName) {
        this(nThreads, poolName, null, SelectorProvider.provider(), DefaultSelectStrategy.INSTANCE);
    }

    public NioEventLoopGroup(int nThreads, String poolName, Executor executor, Object... args) {
        if (executor == null) {
            executor = new ThreadPerTaskExecutor(new DefaultThreadFactory(getClass(), poolName));
        }

        this.children = new NioEventLoop[nThreads];
        for (int i = 0; i < nThreads; i++) {
            this.children[i] = newChild(executor, args);
        }
    }

    private EventExecutor newChild(Executor executor, Object[] args) {
        return new NioEventLoop(this, executor, (SelectorProvider) args[0], (SelectStrategy) args[1]);
    }

    @Override
    public EventExecutor next() {
        return this.children[(int)Math.abs(idx.incrementAndGet() % this.children.length)];
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return next().register(channel);
    }

}
