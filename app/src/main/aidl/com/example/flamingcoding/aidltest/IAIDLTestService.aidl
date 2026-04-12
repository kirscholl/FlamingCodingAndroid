// IAIDLTestService.aidl
package com.example.flamingcoding.aidltest;
import com.example.flamingcoding.aidltest.Book;

// Declare any non-default types here with import statements

interface IAIDLTestService {
    /**
     * 添加一本书
     */
    void addBook(in Book book);

    /**
     * 获取所有书籍列表
     */
    List<Book> getBookList();

    /**
     * 获取书籍数量
     */
    int getBookCount();

    /**
     * 简单的计算方法
     */
    int calculate(int a, int b);
}