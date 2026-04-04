package com.example.pffbrowser.temptest

open class PfKotlinTest {
    private constructor() {

    }

    class ChildTest {
        fun test() {
            val test = PfKotlinTest()
        }
    }

    fun test() {
        val pfJavaTest = PfJavaTest()
        val testStr1: String = pfJavaTest.pfTestString
        val testStr2 = pfJavaTest.getString(false)
        testString(testStr1, testStr2)
    }

    fun testString(str1: String, str2: String) {

    }
}


sealed class SealedTest {

}