package io.netty.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public interface Channel {

    SelectionKey getSelectionKey();

    DefaultChannelPipeline pipeline();

    EventLoop eventLoop();

    SelectableChannel javaChannel();

    Unsafe unsafe();

    void doBind(InetSocketAddress socketAddress) throws Exception;

    /**
     * 内部接口，聚合在Channel中协助读写相关的操作，是一个内部辅助类，不应被netty框架的上层使用者调用
     */
    interface  Unsafe {
        void read() throws IOException;

        /**
         * 将当前的channel注册到EventLoop的多路复用器上，然后调用DefaultChannelPipeline的fireChannelRegistered方法；
         * 如果channel被激活，则调用DefaultChannelPipeline的fireChannelActive方法；
         *
         * @param eventLoop
         * @param promise
         */
        void register(EventLoop eventLoop, ChannelPromise promise);
    }

}
