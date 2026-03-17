package com.example.pffbrowser.home

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentHomeBinding
import com.example.pffbrowser.request.search.IHotSearchService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
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
            findNavController().navigate(R.id.pb_action_homefragment_to_searchfragment)
        }
    }

    fun test() {
        val service = retrofit.create(IHotSearchService::class.java)
        lifecycleScope.async {
            service.getHotSearchWord()
        }
    }
}