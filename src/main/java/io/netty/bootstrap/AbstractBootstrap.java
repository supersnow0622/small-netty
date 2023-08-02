package io.netty.bootstrap;

import io.netty.channel.*;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

public abstract class AbstractBootstrap<B extends AbstractBootstrap<B,C>, C extends Channel>   {
    /**
     * 父线程组，主要用于处理客户端的链接
     */
    volatile EventLoopGroup parentGroup;

    /**
     * 工厂类，用于创建NioServerSocketChannel实例
     */
    private volatile ReflectiveChannelFactory channelFactory;

    /**
     * 自定义handler，组装pipeline
     */
    private volatile ChannelHandler handler;

    public B group(EventLoopGroup parentGroup) {
        this.parentGroup = parentGroup;
        return (B) this;
    }

    public B channel(Class clazz) {
        this.channelFactory = new ReflectiveChannelFactory<>(clazz);
        return (B) this;
    }

    public void bind(int port) {
        bind(new InetSocketAddress(port));
    }

    public void bind(InetSocketAddress socketAddress) {
        ChannelFuture channelFuture = initAndRegister();
        doBind(channelFuture, socketAddress);
    }

    private void doBind(ChannelFuture channelFuture, InetSocketAddress socketAddress) {
        Channel channel = channelFuture.channel();
        if (channelFuture.isDone()) {
            doBind0(channel, socketAddress);
        } else {
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    doBind0(channel, socketAddress);
                }
            });
        }
    }

    private void doBind0(Channel channel, InetSocketAddress socketAddress) {
        try {
            channel.doBind(socketAddress);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建NioServerSocketChannel实例，然后注册到parentGroup线程组中
     *
     * @return
     */
    protected ChannelFuture initAndRegister() {
        Channel channel = channelFactory.newChannel();
        try {
            init(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parentGroup.register(channel);
    }

    abstract void init(Channel channel) throws Exception;


    public B handler(ChannelHandler channelHandler) {
        this.handler = channelHandler;
        return (B)this;
    }

    public ChannelHandler getHandler() {
        return handler;
    }
}
