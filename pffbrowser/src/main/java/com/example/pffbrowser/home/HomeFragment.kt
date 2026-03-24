package com.example.pffbrowser.home

import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentHomeBinding
import com.example.pffbrowser.ext.safeNavigateThrottle
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment<PbFragmentHomeBinding, HomeViewModel>() {

    companion object {
        const val TAG = "HomeFragment"
    }

    @Inject
    lateinit var retrofit: Retrofit

    override fun PbFragmentHomeBinding.setOnClickListener() {
        mViewBinding.searchBtn.setOnClickListener {
            findNavController().safeNavigateThrottle(
                R.id.HomeFragment,
                R.id.pb_action_homefragment_to_searchfragment
            )
        }

        mViewBinding.btnTest1.setOnClickListener {

        }

        mViewBinding.btnTest2.setOnClickListener {

        }
    }
}