package com.example.flamingcoding.androidTrials

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flamingcoding.R

class FragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        // 获取fragment中的按钮, 在这里拿不到fragment.view还未被创建出来 --> 在 onStart()之后的生命周期中可以拿到
//        val fragment = supportFragmentManager.findFragmentById(R.id.leftFrag) as LeftFragment
//        val button = fragment.view?.findViewById<Button>(R.id.buttonLeftFragment)
//        val button = findViewById<Button>(R.id.buttonChangeFragment)
//        replaceFragment(RightFragment())
//        button?.setOnClickListener {
//            replaceFragment(AnotherRightFragment())
//        }
    }

    override fun onStart() {
        super.onStart()
        // 在Activity中获取Fragment
        // 获取fragment中的按钮
        val fragment = supportFragmentManager.findFragmentById(R.id.leftFrag) as LeftFragment
        val button = fragment.view?.findViewById<Button>(R.id.buttonLeftFragment)
//        val button = findViewById<Button>(R.id.buttonChangeFragment)
        replaceFragment(RightFragment())
        button?.setOnClickListener {
            replaceFragment(AnotherRightFragment())
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.rightLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}