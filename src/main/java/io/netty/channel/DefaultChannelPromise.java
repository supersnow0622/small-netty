package io.netty.channel;

import io.netty.channel.nio.NioEventLoop;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DefaultChannelPromise extends ChannelPromise {

    private final Channel channel;

    private final NioEventLoop executor;

    public DefaultChannelPromise(Channel channel, NioEventLoop executor) {
        this.channel = channel;
        this.executor = executor;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
