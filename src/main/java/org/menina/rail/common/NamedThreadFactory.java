package org.menina.rail.common;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhenghao on 2018/12/13.
 */
@Slf4j
public class NamedThreadFactory implements ThreadFactory{

    private String name;

    private AtomicInteger amotic = new AtomicInteger(0);

    public NamedThreadFactory(String name) {
        Preconditions.checkNotNull(name, "not null");
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread newThread = new Thread(Thread.currentThread().getThreadGroup(), r, Joiner.on("-").join(name, amotic.incrementAndGet()));
        newThread.setPriority(Thread.NORM_PRIORITY);
        newThread.setDaemon(true);
        newThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught Exception for thread {}, error message: {}",
                        t.getName(),
                        e.getMessage(),
                        e);
            }
        });

        return newThread;
    }
}
