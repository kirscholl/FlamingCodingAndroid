package com.example.flamingcoding.thread;

import java.util.concurrent.locks.ReentrantReadWriteLock;

class ReentrantLockTest {

    ReentrantReadWriteLock reentrantLock = new ReentrantReadWriteLock();
    ReentrantReadWriteLock.ReadLock readLock = reentrantLock.readLock();
    ReentrantReadWriteLock.WriteLock writeLock = reentrantLock.writeLock();

    int value = 0;

//    private void test() {
//        reentrantLock.lock();
//        try {
//            value++;
//            //... 可能报错
//        } finally {
//            // 解锁
//            reentrantLock.unlock();
//        }
//    }

    // 读，写分离锁
    // 1 写 别的不能写
    // 1 写 别的不能读
    // 1 读 别的是可以读的
    private void test() {
        reentrantLock.writeLock();
        try {
            value++;
            //... 可能报错
        } finally {
            // 解锁
            reentrantLock.writeLock();
        }
    }

}
