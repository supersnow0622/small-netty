package io.netty.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class ThreadPerTaskExecutor implements Executor {

    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void execute(Runnable r) {
        threadFactory.newThread(r).start();
    }
}
