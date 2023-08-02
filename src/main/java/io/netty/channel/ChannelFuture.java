package io.netty.channel;

import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Future;

public interface ChannelFuture extends Future<Void>{
    Channel channel();

    ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener);
}
