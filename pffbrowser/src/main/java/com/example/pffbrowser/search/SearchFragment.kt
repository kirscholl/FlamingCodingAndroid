package com.example.pffbrowser.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentSearchBinding

class SearchFragment : BaseFragment() {

    companion object {
        const val TAG = "SearchFragment"
    }

    lateinit var binding: PbFragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PbFragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.pb_action_searchfragment_to_homefragment)
        }

        binding.btnSearch.setOnClickListener {
            if (!binding.editTextSearch.text.isEmpty()) {
                val searchWord = binding.editTextSearch.text.toString()
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

        binding.rootLayout.setOnClickListener {
            binding.editTextSearch.clearFocus()
        }

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard(binding.editTextSearch)
            } else {
                hideKeyboard(binding.editTextSearch)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.editTextSearch.requestFocus()
    }

    override fun onPause() {
        super.onPause()
        binding.editTextSearch.clearFocus()
    }

    fun showKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}