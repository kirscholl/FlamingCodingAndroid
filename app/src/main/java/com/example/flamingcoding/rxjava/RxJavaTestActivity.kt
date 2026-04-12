package com.example.flamingcoding.rxjava

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

class RxJavaTestActivity : AppCompatActivity() {
    companion object {
        const val TAG = "RxJavaTestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rx_java_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        singleJustTest()
    }

    private fun singleJustTest() {
        val singleIntJust = Single.just(1)
        val singleStringJust3: Single<String> =
            singleIntJust.observeOn(Schedulers.io())
                .map { t -> t.toString() }
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .map { t -> t + "123" }
                .subscribeOn(Schedulers.newThread())
        val singleStringJust: Single<String> = singleIntJust.map { t -> t.toString() }
        val singleStringJust2 = singleIntJust.map<String>(object : Function<Int, String> {
            override fun apply(t: Int): String {
                return t.toString()
            }
        })
        singleStringJust.subscribe(object : SingleObserver<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe Disposable: $d")
            }

            override fun onSuccess(t: String) {
                Log.d(TAG, "onSuccess t: $t")
            }

            override fun onError(e: Throwable) {
            }
        })
    }
}