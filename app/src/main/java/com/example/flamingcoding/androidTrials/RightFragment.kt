package com.example.flamingcoding.androidTrials

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flamingcoding.R

class RightFragment : Fragment() {

    override fun onAttach(context: Context) {
        Log.d("RightFragment", "Fragment Life Cycle: onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("RightFragment", "Fragment Life Cycle: onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("RightFragment", "Fragment Life Cycle: onCreateView")
        // 在Fragment中通过getActivity()获取Activity
        val fragmentActivity = activity as FragmentActivity
        return inflater.inflate(R.layout.right_fragment, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//    }

    override fun onStart() {
        Log.d("RightFragment", "Fragment Life Cycle: onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("RightFragment", "Fragment Life Cycle: onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("RightFragment", "Fragment Life Cycle: onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("RightFragment", "Fragment Life Cycle: onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("RightFragment", "Fragment Life Cycle: onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("RightFragment", "Fragment Life Cycle: onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d("RightFragment", "Fragment Life Cycle: onDetach")
        super.onDetach()
    }

    fun fragmentLifeCycle() {
        // 当一个Activity进入暂停状态时（由于另一个未占满屏幕的Activity被添加到了栈顶），与它相关联的Fragment就会进入暂停状态

        // 当一个Activity进入停止状态时，与它相关联的Fragment就会进入停止状态，
        // 或者通过调用FragmentTransaction的remove()、replace()方法将Fragment从Activity中移除，但在事务提交之前调用了addToBackStack()方法，
        // 这时的Fragment也会进入停止状态。总的来说，进入停止状态的Fragment对用户来说是完全不可见的，有可能会被系统回收。
    }
}