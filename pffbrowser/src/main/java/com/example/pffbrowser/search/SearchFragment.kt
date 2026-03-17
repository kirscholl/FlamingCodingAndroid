package com.example.pffbrowser.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentSearchBinding
import com.example.pffbrowser.request.search.HotSearchData
import com.example.pffbrowser.utils.AnimationUtils.generateTabShakeAnim
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

    override fun PbFragmentSearchBinding.initView() {

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

        viewBinding.btnEdit.setOnClickListener {
            toggleEditMode()
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
            viewModel.hotSearchDataList = it.data
            setHotSearchView()
        }
    }

    fun setHotSearchView() {
        viewBinding.flexboxHistory.removeAllViews()
        viewModel.tagViews.clear()

        viewModel.hotSearchDataList.forEach { data ->
            val tagView = layoutInflater.inflate(
                R.layout.pb_hot_search_item,
                viewBinding.flexboxHistory,
                false
            )
            val tvTag = tagView.findViewById<TextView>(R.id.tv_tag)
            val ivDelete = tagView.findViewById<ImageView>(R.id.iv_delete)

            tvTag.text = data.name

            // 设置删除点击事件
            ivDelete.setOnClickListener {
                deleteTag(data)
            }

            // 标签点击事件（用于搜索，可选）
            tagView.setOnClickListener {
                if (!viewModel.isEditMode) {
                    // 执行搜索操作
                    Toast.makeText(context, "搜索：${data.name}", Toast.LENGTH_SHORT).show()
                }
            }

            viewBinding.flexboxHistory.addView(tagView)
            viewModel.tagViews.add(tagView)
        }

        // 如果当前处于编辑模式，需更新删除按钮可见性并启动抖动
        if (viewModel.isEditMode) {
            viewModel.tagViews.forEach { switchTabItemViewEdit(it) }
        }
    }

    // 切换编辑模式
    private fun toggleEditMode() {
        viewModel.isEditMode = !viewModel.isEditMode
        viewBinding.btnEdit.text = if (viewModel.isEditMode) "完成" else "编辑"

        if (viewModel.isEditMode) {
            viewModel.tagViews.forEach { view ->
                switchTabItemViewEdit(view)
            }
        } else {
            viewModel.tagViews.forEach { view ->
                switchTabItemViewNormal(view)
            }
        }
    }

    // 控制单个标签的删除按钮显示与抖动动画
    private fun switchTabItemViewEdit(view: View) {
        val ivDelete = view.findViewById<ImageView>(R.id.iv_delete)
        ivDelete.visibility = View.VISIBLE
        val anim = generateTabShakeAnim(view)
        viewModel.hotSearchAnimMap[view] = anim
        anim.start()
    }

    private fun switchTabItemViewNormal(view: View) {
        val ivDelete = view.findViewById<ImageView>(R.id.iv_delete)
        ivDelete.visibility = View.GONE
        viewModel.hotSearchAnimMap[view]?.cancel()
        view.rotation = 0f  // 复位
    }

    // 删除标签
    private fun deleteTag(data: HotSearchData) {
        val position = viewModel.hotSearchDataList.indexOf(data)
        if (position != -1) {
            viewModel.hotSearchDataList.removeAt(position)
            // 移除对应的View
            val needRemoveView = viewBinding.flexboxHistory[position]
            // 关闭动画，移除动画
            viewModel.hotSearchAnimMap[needRemoveView]?.cancel()
            viewModel.hotSearchAnimMap.remove(needRemoveView)
            viewBinding.flexboxHistory.removeViewAt(position)
            viewModel.tagViews.removeAt(position)
        }
    }

}