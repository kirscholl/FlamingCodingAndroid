package com.example.flamingcoding.threadAsync;

public class SynchronizedTest {

    static final Object staticLock = new Object();

    private static void stest1() {
        synchronized (staticLock) {
            // ...
        }
    }

    // ↑ 等价于
    private static synchronized void stest2() {
        // ...
    }

    // ↑ 等价于
    private static void stest3() {
        synchronized (SynchronizedTest.class) {
            // ...
        }

    }

    // 考虑场景increaseX与minusX要不能同时访问，increaseY与minusY要不能同时访问
    // 但是increaseX、minusX与readX可以同时访问
    // synchronized是为对象添加monitor监控器，监控器相同的两个方法则互斥

    private int x = 0;
    private int y = 0;
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();


//    private void increaseX() {
//        synchronized (this) {
//            x = x + 1;
//        }
//    }
    // ↑ 等价于
//    private synchronized void increaseX() {
//        x = x + 1;
//        y = y + 1;
//    }

    private void increaseX() {
        synchronized (lock1) {
            x = x + 1;
        }
    }

    private void minusX() {
        synchronized (lock1) {
            y = y - 1;
        }
    }


    private void increaseY() {
        synchronized (lock2) {
            x = x + 1;
        }
    }


    private void minusY() {
        synchronized (lock2) {
            y = y - 1;
        }
    }

    private int readX() {
        return x;
    }

    private int readY() {
        return y;
    }
}
