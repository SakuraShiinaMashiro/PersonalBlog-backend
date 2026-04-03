package com.czf.blog.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 异步任务线程池单例工具
 * @author czf
 * @date 2026-04-03
 */
public final class AsyncExecutor {
    private static final int MIN_THREADS = 2;
    private static final int QUEUE_CAPACITY = 200;
    private static final String THREAD_NAME_PREFIX = "mail-async-";

    private static volatile ThreadPoolExecutor executor;

    private AsyncExecutor() {
    }

    /**
     * 获取线程池单例。
     *
     * @return 线程池实例
     */
    public static ThreadPoolExecutor getInstance() {
        if (executor == null) {
            synchronized (AsyncExecutor.class) {
                if (executor == null) {
                    int threads = Math.max(MIN_THREADS, Runtime.getRuntime().availableProcessors());
                    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
                    ThreadFactory factory = new ThreadFactory() {
                        private final AtomicInteger index = new AtomicInteger(1);

                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName(THREAD_NAME_PREFIX + index.getAndIncrement());
                            return thread;
                        }
                    };
                    executor = new ThreadPoolExecutor(
                            threads,
                            threads,
                            0L,
                            TimeUnit.MILLISECONDS,
                            queue,
                            factory,
                            new ThreadPoolExecutor.CallerRunsPolicy()
                    );
                }
            }
        }
        return executor;
    }

    /**
     * 提交异步任务。
     *
     * @param task 待执行任务
     */
    public static void execute(Runnable task) {
        getInstance().execute(task);
    }
}
