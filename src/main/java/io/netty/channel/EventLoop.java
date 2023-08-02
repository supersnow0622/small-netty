package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;

import java.nio.channels.Selector;

public interface EventLoop extends EventExecutor {

    Selector selector();
}
