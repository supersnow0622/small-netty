package io.netty.channel;

/**
 * 提供抽象类给使用方，由使用方自行初始化通道中事件的处理器
 */
public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter {

    public abstract void initChannel(C ch) throws Exception;

}
