package com.example.flamingcoding.fragmentnavigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.flamingcoding.R

class SecondNavigationTestFragment : Fragment() {

    companion object {
        const val TAG = "SecondNavigationTestFragment"
        fun newInstance() = SecondNavigationTestFragment()
    }

    private val viewModel: SecondNavigationTestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_second_navigation_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val testString = this.arguments?.getString("testString")
        val testInt = this.arguments?.getInt("testInt")
        Log.d(TAG, "testString: $testString")
        Log.d(TAG, "testString: $testInt")
    }
}