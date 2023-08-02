package io.netty.channel;

import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Future;

public abstract class ChannelPromise implements ChannelFuture {

    private volatile boolean done;
    protected Object listener;

    public abstract Channel channel();

    @Override
    public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        if (this.listener == null) {
            this.listener = listener;
        }

        if (isDone()) {
            notifyListener();
        }

        return this;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void notifyListener() {
        if (listener == null) {
            return;
        }
        try {
            ((GenericFutureListener)listener).operationComplete(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
