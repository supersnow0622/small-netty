package io.netty.channel.nio;

import io.netty.channel.*;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.IntSupplier;

public class NioEventLoop implements EventLoop {

    private final EventLoopGroup parent;

    /**
     * NioEventLoop创建时通过父类构造器传入进来
     */
    private final Executor executor;

    private final SelectorProvider provider;

    private final SelectStrategy selectStrategy;

    private Selector selector;

    /**
     * NioEventLoop任务队列
     */
    private final Queue<Runnable> taskQueue;

    /**
     * executor执行创建新线程并执行task时，给当前NioEventLoop绑定当前thread
     */
    private volatile Thread thread;

    public NioEventLoop(EventLoopGroup parent, Executor executor, SelectorProvider provider, SelectStrategy selectStrategy) {
        this.parent = parent;
        this.executor = executor;
        this.provider = provider;
        this.selectStrategy = selectStrategy;

        try {
            this.selector = provider.openSelector();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.taskQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
    }


    @Override
    public ChannelFuture register(Channel channel) {
        ChannelPromise promise = new DefaultChannelPromise(channel, this);
        channel.unsafe().register(this, promise);
        return promise;
    }

    @Override
    public Selector selector() {
        return selector;
    }

    private void runAllTasks() {
        while (true) {
            Runnable task = taskQueue.poll();
            if (task == null) {
                break;
            }
            task.run();
        }
    }

    @Override
    public boolean inEventLoop() {
        return this.thread == Thread.currentThread();
    }

    private final IntSupplier selectNowSupplier = new IntSupplier() {
        @Override
        public int getAsInt() {
            try {
                return selector.selectNow();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Override
    public void execute(Runnable command) {
        taskQueue.offer(command);
        boolean inEventLoop = inEventLoop();
        // 创建新线程处理任务
        if (!inEventLoop) {
            executor.execute(this::run);
        }

        // 线程已存在，则将Selector从阻塞态唤醒
//        selector.wakeup();
    }

    private void run() {
        thread = Thread.currentThread();

        while (true) {
            try {
                // taskQueue中有任务，则先处理通道中就绪的任务，再执行taskQueue中的任务；
                // taskQueue中无任务，则阻塞获取通道中的任务
                int strategy = selectStrategy.calculateStrategy(selectNowSupplier, !taskQueue.isEmpty());
                switch (strategy) {
                    case SelectStrategy.CONTINUE: continue;
                    case SelectStrategy.BUSY_WAIT:
                    case SelectStrategy.SELECT:
                        // 阻塞到至少有一个事件就绪
                        strategy = selector.select();
                        break;
                }

                if (strategy > 0) {
                    processSelectedKeys();
                }

                runAllTasks();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processSelectedKeys() throws IOException {
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            iterator.remove();

            Object attachment = selectionKey.attachment();
            if (attachment instanceof AbstractNioChannel) {
                processSelectedKey(selectionKey, (AbstractNioChannel)attachment);
            }
        }
    }

    private void processSelectedKey(SelectionKey selectionKey, AbstractNioChannel abstractNioChannel) throws IOException {
        Channel.Unsafe unsafe = abstractNioChannel.unsafe();

        if (selectionKey.readyOps() == 0) {
            return;
        }

        // 服务端接受客户端连接或客户端连接可读
        if (selectionKey.isAcceptable() || selectionKey.isReadable()) {
            unsafe.read();
            return;
        }

        // 客户端连接服务端
        if (selectionKey.isConnectable()) {
            try {
                SocketChannel socketChannel = (SocketChannel) abstractNioChannel.javaChannel();
                if (socketChannel.finishConnect()) {
                    System.out.println("客户端和服务端已建立连接");
                    // 将客户端channel状态从SelectionKey.OP_CONNECT改为SelectionKey.OP_READ
                    selectionKey.interestOps(SelectionKey.OP_READ);

                    // 链接成功，触发pipeline中的active方法，然后向服务端发送消息
                    abstractNioChannel.pipeline().fireChannelActive();
                } else {
                    System.exit(1);// 连接失败，进程退出
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
