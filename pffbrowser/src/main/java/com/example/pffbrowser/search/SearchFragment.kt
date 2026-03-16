package com.example.pffbrowser.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<PbFragmentSearchBinding, SearchViewModel>() {

    companion object {
        const val TAG = "SearchFragment"
    }

    @Inject
    lateinit var retrofit: Retrofit

    override fun onResume() {
        super.onResume()
        viewBinding.editTextSearch.requestFocus()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.editTextSearch.clearFocus()
    }

    fun showKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun PbFragmentSearchBinding.setOnClickListener() {
        viewBinding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.pb_action_searchfragment_to_homefragment)
        }

        viewBinding.btnSearch.setOnClickListener {
            if (!viewBinding.editTextSearch.text.isEmpty()) {
                val searchWord = viewBinding.editTextSearch.text.toString()
                val bundle = Bundle().apply {
                    putString("searchWord", searchWord)
                }
                findNavController().navigate(
                    R.id.pb_action_searchfragment_to_searchresultfragment,
                    bundle
                )
            } else {
                Toast.makeText(context, "请输入搜索词", Toast.LENGTH_SHORT).show()
            }
        }

        viewBinding.rootLayout.setOnClickListener {
            viewBinding.editTextSearch.clearFocus()
        }

        viewBinding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard(viewBinding.editTextSearch)
            } else {
                hideKeyboard(viewBinding.editTextSearch)
            }
        }
    }

    override fun initRequestData() {
        super.initRequestData()
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.hotSearchLiveData.observe(this) {
            // todo 初始化搜索
            println("初始化搜索热词 $it")
        }
    }
}