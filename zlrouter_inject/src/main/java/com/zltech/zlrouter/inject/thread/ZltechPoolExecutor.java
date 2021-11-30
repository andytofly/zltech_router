package com.zltech.zlrouter.inject.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ZltechPoolExecutor {

    private ThreadPoolExecutor executor;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ZlRouter #" + mCount.getAndIncrement());
        }
    };

    public void execute(Runnable runnable){
        executor.execute(runnable);
    }

    //核心线程和最大线程都是cpu核心数+1
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int MAX_CORE_POOL_SIZE = CPU_COUNT + 1;
    //存活30秒 回收线程
    private static final long SURPLUS_THREAD_LIFE = 30L;


    /***SingleTon start***/
    private ZltechPoolExecutor() {
        /**
         * ArrayBlockingQueue: 一个阻塞队列，用来存储等待执行的任务，如果当前对线程的需求超过了corePoolSize大小，才会放在这里；
         */
        int corePoolSize = MAX_CORE_POOL_SIZE;
        executor = new ThreadPoolExecutor(corePoolSize,
                corePoolSize, SURPLUS_THREAD_LIFE, TimeUnit.SECONDS, new
                ArrayBlockingQueue<Runnable>(64), sThreadFactory);
        //核心线程也会被销毁
        executor.allowCoreThreadTimeOut(true);
    }

    private static class SingleTonHoler {
        private static ZltechPoolExecutor INSTANCE = new ZltechPoolExecutor();
    }

    public static ZltechPoolExecutor getInstance() {
        return SingleTonHoler.INSTANCE;
    }
    /***end***/
}

























