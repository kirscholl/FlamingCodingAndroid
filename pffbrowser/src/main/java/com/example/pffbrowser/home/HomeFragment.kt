package com.example.pffbrowser.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentHomeBinding
import com.example.pffbrowser.request.search.HotSearchWordService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    companion object {
        const val TAG = "HomeFragment"
    }

    @Inject
    lateinit var retrofit: Retrofit

    lateinit var binding: PbFragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PbFragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchBtn.setOnClickListener {
//            findNavController().navigate(R.id.pb_action_homefragment_to_searchfragment)
            test()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    fun test() {
        val service = retrofit.create(HotSearchWordService::class.java)
        runBlocking {
            val res = service.getHotSearchWord()
            println(res.toString())
        }
    }
}