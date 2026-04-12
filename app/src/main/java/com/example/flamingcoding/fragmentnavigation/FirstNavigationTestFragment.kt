package com.example.flamingcoding.fragmentnavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
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
        binding.firstNavTestBtn1.setOnClickListener {
            val bundle = Bundle().apply {
                putString("testString", "testString1")
                putInt("testInt", 1)
            }
            findNavController().safeNavigate(
                R.id.FirstFragment,
                R.id.action_FirstFragment_to_SecondFragment,
                bundle,
            )
        }
        binding.firstNavTestBtn2.setOnClickListener {
            val bundle = Bundle().apply {
                putString("testString", "testString2")
                putInt("testInt", 2)
            }
            findNavController().safeNavigate(
                R.id.FirstFragment,
                R.id.action_FirstFragment_to_ThirdFragment,
                bundle
            )
        }
    }

    fun NavController.safeNavigate(
        @IdRes currentDestinationId: Int,
        @IdRes resId: Int,
        args: Bundle?,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ) {
        // 只有当前 destination 仍然是预期的页面时才导航
        if (currentDestination?.id == currentDestinationId) {
            navigate(resId, args, navOptions, navigatorExtras)
        }
    }
}