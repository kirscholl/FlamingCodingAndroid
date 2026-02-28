package com.example.flamingcoding.rengWuXianKotlin;

import android.util.Log;

public class ReWuXianJavaAssistant {

    static String TAG = "ReWuXianJavaAssistant";

    int testInt = 1;
    String testString = "test";
    String testNullString;

    ReWuXianJavaAssistant() {

    }

    ReWuXianJavaAssistant(int paramInt, String paramString) {

    }

    public void testFunction() {
        if (testNullString != null) {
            Log.d(TAG, testNullString);
        }
    }
}
