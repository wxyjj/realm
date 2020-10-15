package com.wxy.realm.support;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工程定义
 *
 * @Author wxy
 * @Date 2020/9/22 11:16
 * @Version 1.0
 */
public class WorkThreadFactory implements ThreadFactory {
    /**
     * 线程池编号
     */
    private static final AtomicInteger POOL = new AtomicInteger(1);
    /**
     * 线程编号
     */
    private static final AtomicInteger THREAD = new AtomicInteger(1);
    /**
     * 线程组
     */
    private final ThreadGroup group;
    /**
     * 线程名
     */
    private final String name;
    /**
     * 是否守护进程
     */
    private final boolean isDaemon;

    public WorkThreadFactory(String threadName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.name = threadName + "-" + String.format("%03d", POOL.getAndIncrement()) + "-t-";
        this.isDaemon = false;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, name + String.format("%03d", THREAD.getAndIncrement()), 0);
        t.setDaemon(isDaemon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
