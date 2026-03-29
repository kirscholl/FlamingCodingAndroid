package com.example.flamingcoding.kotlinTrials;

public final class HungryTestSingleton {
    /**
     * 实例对象
     */
    private static HungryTestSingleton instance = new HungryTestSingleton();

    // 编译时期常量，不会触发类初始化！！！
    public static final String TEST_STRING = "HungryTestSingleton";
    // 静态变量，会触发类的初始化！！！
    public static String TEST_STRING2 = "HungryTestSingleton";

    /**
     * 禁用构造方法
     */
    private HungryTestSingleton() {
        System.out.println("HungryTestSingleton init");
    }

    /**
     * 获取单例对象, 直接返回已创建的实例
     *
     * @return instance 本类的实例
     */
    public static HungryTestSingleton getInstance() {
        return instance;
    }
}