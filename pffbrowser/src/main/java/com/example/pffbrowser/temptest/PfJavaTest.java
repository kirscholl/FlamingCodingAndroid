package com.example.pffbrowser.temptest;

import com.example.pffbrowser.ext.ExtKt;

public class PfJavaTest {

    public String pfTestString = null;

    private void test() {
        String res = "123";
        ExtKt.strExtTest("Hello", "123");
    }

    public String getString(boolean flag) {
        if (flag) {
            return "123";
        } else {
            return null;
        }
    }
}

