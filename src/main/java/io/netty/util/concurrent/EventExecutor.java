package io.netty.util.concurrent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.Executor;

public interface EventExecutor extends Executor {

    ChannelFuture register(Channel channel);

    boolean inEventLoop();
}
