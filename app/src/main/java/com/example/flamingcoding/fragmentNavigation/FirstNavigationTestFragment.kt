package com.example.flamingcoding.fragmentNavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.flamingcoding.R
import com.example.flamingcoding.databinding.FragmentFirstNavigationTestBinding

class FirstNavigationTestFragment : Fragment() {

    lateinit var binding: FragmentFirstNavigationTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstNavigationTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.firstNavTestBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("testString", "testString")
                putInt("testInt", 1)
            }
            Navigation.findNavController(it)
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        }
    }
}