package com.example.pffbrowser.search

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentSearchBinding
import com.example.pffbrowser.request.search.HotSearchData
import com.example.pffbrowser.utils.AnimationUtil.generateTabShakeAnim
import com.example.pffbrowser.utils.KeyboardUtil.hideKeyboard
import com.example.pffbrowser.utils.KeyboardUtil.showKeyboard
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
        mViewBinding.editTextSearch.requestFocus()
    }

    override fun onPause() {
        super.onPause()
        mViewBinding.editTextSearch.clearFocus()
    }

    override fun PbFragmentSearchBinding.initView() {

    }

    override fun PbFragmentSearchBinding.setOnClickListener() {
        mViewBinding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.pb_action_searchfragment_to_homefragment)
        }

        mViewBinding.btnSearch.setOnClickListener {
            if (!mViewBinding.editTextSearch.text.isEmpty()) {
                val searchWord = mViewBinding.editTextSearch.text.toString()
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

        mViewBinding.rootLayout.setOnClickListener {
            mViewBinding.editTextSearch.clearFocus()
        }

        mViewBinding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard(mViewBinding.editTextSearch)
            } else {
                hideKeyboard(mViewBinding.editTextSearch)
            }
        }

        mViewBinding.btnEdit.setOnClickListener {
            toggleEditMode()
        }
    }

    override fun initRequestData() {
        super.initRequestData()
    }

    override fun initObserver() {
        super.initObserver()
        mViewModel.hotSearchLiveData.observe(this) {
            // todo 初始化搜索
            println("初始化搜索热词 $it")
            mViewModel.hotSearchDataList = it.data
            setHotSearchView()
        }
    }

    fun setHotSearchView() {
        mViewBinding.flexboxHistory.removeAllViews()
        mViewModel.tagViews.clear()
        mViewModel.hotSearchDataList.forEach { data ->
            val tagView = layoutInflater.inflate(
                R.layout.pb_hot_search_item,
                mViewBinding.flexboxHistory,
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
                if (!mViewModel.isEditMode) {
                    // 执行搜索操作
                    Toast.makeText(context, "搜索：${data.name}", Toast.LENGTH_SHORT).show()
                }
            }

            mViewBinding.flexboxHistory.addView(tagView)
            mViewModel.tagViews.add(tagView)
        }

        // 如果当前处于编辑模式，需更新删除按钮可见性并启动抖动
        if (mViewModel.isEditMode) {
            mViewModel.tagViews.forEach { switchTabItemViewEdit(it) }
        }
    }

    // 切换编辑模式
    private fun toggleEditMode() {
        mViewModel.isEditMode = !mViewModel.isEditMode
        mViewBinding.btnEdit.text = if (mViewModel.isEditMode) "完成" else "编辑"

        if (mViewModel.isEditMode) {
            mViewModel.tagViews.forEach { view ->
                switchTabItemViewEdit(view)
            }
        } else {
            mViewModel.tagViews.forEach { view ->
                switchTabItemViewNormal(view)
            }
        }
    }

    // 控制单个标签的删除按钮显示与抖动动画
    private fun switchTabItemViewEdit(view: View) {
        val ivDelete = view.findViewById<ImageView>(R.id.iv_delete)
        ivDelete.visibility = View.VISIBLE
        val anim = generateTabShakeAnim(view)
        mViewModel.hotSearchAnimMap[view] = anim
        anim.start()
    }

    private fun switchTabItemViewNormal(view: View) {
        val ivDelete = view.findViewById<ImageView>(R.id.iv_delete)
        ivDelete.visibility = View.GONE
        mViewModel.hotSearchAnimMap[view]?.cancel()
        view.rotation = 0f  // 复位
    }

    // 删除标签
    private fun deleteTag(data: HotSearchData) {
        val position = mViewModel.hotSearchDataList.indexOf(data)
        if (position != -1) {
            mViewModel.hotSearchDataList.removeAt(position)
            // 移除对应的View
            val needRemoveView = mViewBinding.flexboxHistory[position]
            // 关闭动画，移除动画
            mViewModel.hotSearchAnimMap[needRemoveView]?.cancel()
            mViewModel.hotSearchAnimMap.remove(needRemoveView)
            mViewBinding.flexboxHistory.removeViewAt(position)
            mViewModel.tagViews.removeAt(position)
        }
    }
}