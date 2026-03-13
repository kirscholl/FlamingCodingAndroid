package com.example.flamingcoding.threadAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NewThreadTest {

    private void newThread1() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("newThread1");
            }
        };
        thread.start();
    }

    private void newThread2() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("newThread2");
            }
        };
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        thread1.start();
        thread2.start();
    }

    private void newThread3() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("newThread3");
            }
        };
        Executor executor2 = Executors.newCachedThreadPool();
        executor2.execute(runnable);
        Executor executor3 = Executors.newSingleThreadExecutor();
        executor3.execute(runnable);
        Executor executor4 = Executors.newFixedThreadPool(20);
        executor4.execute(runnable);

        ReentrantLockTest test = new ReentrantLockTest();
    }
}
