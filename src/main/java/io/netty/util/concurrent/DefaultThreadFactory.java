package io.netty.util.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolId = new AtomicInteger();
    private final AtomicInteger nextId = new AtomicInteger();

    private final String prefix;
    private final boolean daemon;

    private final int priority;

    protected final ThreadGroup threadGroup;

    public DefaultThreadFactory(Class<?> clazz, String poolName) {
        this(clazz, poolName,false, Thread.NORM_PRIORITY);
    }

    public DefaultThreadFactory(Class<?> clazz, String poolName, boolean daemon, int priority) {
        this(toPoolName(clazz, poolName), daemon, priority);
    }

    public DefaultThreadFactory(String poolName, boolean daemon, int priority) {
        this(poolName, daemon, priority,
                System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() :
                System.getSecurityManager().getThreadGroup());
    }

    public DefaultThreadFactory(String poolName, boolean daemon, int priority, ThreadGroup threadGroup) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
        }

        this.prefix = poolName + "-" + poolId.incrementAndGet() + "-";
        this.daemon = daemon;
        this.priority = priority;
        this.threadGroup = threadGroup;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, prefix + nextId.incrementAndGet());
        if (thread.isDaemon() != daemon) {
            thread.setDaemon(daemon);
        }
        if (thread.getPriority() != priority) {
            thread.setPriority(priority);
        }
        return thread;
    }

    public static String toPoolName(Class<?> clazz, String poolName) {
        String simpleName = clazz.getSimpleName();
        switch (simpleName.length()) {
            case 0: return poolName + "-" + "unknown";
            case 1: return poolName + "-" + simpleName.toLowerCase();
            default:
                if (Character.isUpperCase(simpleName.charAt(0)) && Character.isLowerCase(simpleName.charAt(1))) {
                    return poolName + "-" + Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
                } else {
                    return poolName + "-" + simpleName;
                }
        }
    }
}
