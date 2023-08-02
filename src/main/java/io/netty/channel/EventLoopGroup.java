package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;

public interface EventLoopGroup {

    EventExecutor next();

    ChannelFuture register(Channel channel);
}
