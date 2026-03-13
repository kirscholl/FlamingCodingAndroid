package com.example.flamingcoding.threadAsync;

public class ThreadInnerCommunicationTest {

    private void test() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                // 在下一段耗时操作之前检查
                if (isInterrupted()) {
                    //...
                    System.out.println("isInterrupted");
                }
                if (Thread.interrupted()) {
                    // 判断之后会置为false
                    System.out.println("Thread.interrupted()");
                }

                // 为什么sleep要try catch
                // 因为会被interrupt()打断
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
//                    e.fillInStackTrace();
                    // 收尾工作
                    // 并且return
                    // ... action
                }
            }
        };
        thread.start();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        thread.interrupt();
    }

    private String str = "";
    private final Object monitor = new Object();

    private void writeStr() {
        synchronized (monitor) {
            str = "write";
        }
    }

    private void getStr() {
        synchronized (monitor) {
            while (str.isEmpty()) {
                try {
                    // wait()的时候会 释放锁！！！进入后台等待队列
                    // notify 和 wait都是Object的方法，为什么？ 因为负责管理等待、通知的是synchronized的monitor
                    monitor.wait();
                } catch (InterruptedException ignored) {

                }
            }
            System.out.println(str);
        }
    }

    // 线程间协作
    private void test2() {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    writeStr();
                    // 写完之后通知，只通知一个！！！
                    notify();
                    // 写完之后通知，通知所有！！！
                    notifyAll();
                } catch (InterruptedException ignored) {

                }
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    getStr();
                } catch (InterruptedException ignored) {

                }
            }
        };
        thread1.start();
//        try {
        // 
//            thread1.join();
//        } catch (InterruptedException ignored) {
//
//        }
        thread2.start();
    }
}
