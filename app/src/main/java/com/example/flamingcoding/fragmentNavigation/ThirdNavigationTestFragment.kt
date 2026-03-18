package com.example.flamingcoding.fragmentNavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flamingcoding.databinding.FragmentThirdNavigationTestBinding

class ThirdNavigationTestFragment : Fragment() {

    lateinit var binding: FragmentThirdNavigationTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThirdNavigationTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnThirdTest.setOnClickListener {
            val bundle = Bundle().apply {
                putString("testString", "testString")
                putInt("testInt", 1)
            }
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        }
    }
}