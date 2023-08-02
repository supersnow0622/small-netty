package io.netty.bootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

public class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, Channel> {
    /**
     * 子线程组，处理socket的读与写
     */
    private NioEventLoopGroup childGroup;
    /**
     * 服务端组装自定义handlers到pipeline
     */
    private ChannelHandler childHandler;

    public ServerBootstrap group(NioEventLoopGroup parentGroup, NioEventLoopGroup childGroup) {
        super.group(parentGroup);
        this.childGroup = childGroup;
        return this;
    }

    public ServerBootstrap childHandler(ChannelHandler channelHandler) {
        this.childHandler = channelHandler;
        return this;
    }

    @Override
    void init(Channel channel) throws Exception {
        DefaultChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new ServerBootstrapAcceptor(childHandler, childGroup));
    }

    private static class ServerBootstrapAcceptor extends ChannelInboundHandlerAdapter {

        private ChannelHandler childHandler;

        private NioEventLoopGroup childGroup;

        public ServerBootstrapAcceptor(ChannelHandler childHandler, NioEventLoopGroup childGroup) {
            this.childHandler = childHandler;
            this.childGroup = childGroup;
        }

        @Override
        public void channelActive(ChannelHandlerContext channelHandlerContext) {

        }

        /**
         * 组装客户端的NioSocketChannel的pipeline
         * @param channelHandlerContext
         * @param msg
         */
        @Override
        public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
            Channel child = (Channel) msg;

            try {
                ((ChannelInitializer) childHandler).initChannel(child);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            childGroup.register(child);
        }
    }
}
