package com.example.flamingcoding.thread;

public class SingletonTest {
    public static SingletonTest instance;

    public static SingletonTest getInstance() {
        if (instance == null) {
            instance = new SingletonTest();
        }
        return instance;
    }

    private SingletonTest() {

    }
}

class SingletonThreadTest {
    public static SingletonThreadTest instance;

    //  线程安全，但是每次取instance都会上锁，所以会性能会差
    public static synchronized SingletonThreadTest getInstance() {
        if (instance == null) {
            instance = new SingletonThreadTest();
        }
        return instance;
    }

    private SingletonThreadTest() {

    }
}

class SingletonThreadTest2 {
    // 添加volatile
    public static volatile SingletonThreadTest2 instance;

    // 只在为空的时候上锁，不为空时直接获取对象
    // 为了防止遇见锁等待的线程再次创建对象，所以在内部再次判断一次
    // 但是初始化过程不是原子操作，在一个线程进行初始化的时候，另一个线程来取，但是对象有了，但是没有初始化完成
    public static synchronized SingletonThreadTest2 getInstance() {
        if (instance == null) {
            synchronized (SingletonThreadTest2.class) {
                if (instance == null) {
                    instance = new SingletonThreadTest2();
                }
            }
        }
        return instance;
    }

    private SingletonThreadTest2() {

    }
}
