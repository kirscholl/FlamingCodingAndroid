package com.example.pffbrowser.download

import android.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentDownloadListBinding
import com.example.pffbrowser.download.db.DownloadTaskEntity
import com.example.pffbrowser.download.db.isCompleted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 下载列表页面
 * 展示所有下载任务，支持暂停/继续/删除操作
 */
@AndroidEntryPoint
class DownloadListFragment : BaseFragment<PbFragmentDownloadListBinding, DownloadListViewModel>() {

    companion object {
        const val TAG = "DownloadListFragment"
    }

    private lateinit var mAdapter: DownloadTaskAdapter

    override fun PbFragmentDownloadListBinding.initView() {
        setupToolbar()
        setupRecyclerView()
    }

    override fun initViewObserver() {
        observeData()
    }

    /**
     * 设置 Toolbar
     */
    private fun setupToolbar() {
        mViewBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * 设置 RecyclerView
     */
    private fun setupRecyclerView() {
        mAdapter = DownloadTaskAdapter(
            onActionClick = { task ->
                // 操作按钮点击（暂停/继续/重试）
                mViewModel.onActionButtonClick(task)
            },
            onItemClick = { task ->
                // Item 点击（打开已完成文件）
                if (task.isCompleted()) {
                    mViewModel.openFile(task)
                }
            },
            onItemLongClick = { task ->
                // 长按删除
                showDeleteConfirmDialog(task)
                true
            }
        )
        mAdapter.viewModel = mViewModel

        mViewBinding.rvDownloadList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DownloadListFragment.mAdapter
        }
    }

    /**
     * 观察数据变化
     */
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 观察下载任务列表
                mViewModel.downloadTasks.collectLatest { tasks ->
                    mAdapter.submitList(tasks)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 观察操作结果
                mViewModel.actionResult.collectLatest { result ->
                    result?.let {
                        when (it) {
                            is DownloadListViewModel.ActionResult.Success -> {
                                // 可以显示 Toast
                            }

                            is DownloadListViewModel.ActionResult.Error -> {
                                // 显示错误提示
                                showErrorDialog(it.message)
                            }
                        }
                        mViewModel.consumeActionResult()
                    }
                }
            }
        }

        // 观察下载速度流（替代轮询）
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.downloadSpeedFlow.collectLatest { speedMap ->
                    mAdapter.updateSpeeds(speedMap)
                }
            }
        }
    }

    /**
     * 显示删除确认对话框
     */
    private fun showDeleteConfirmDialog(task: DownloadTaskEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除下载任务")
            .setMessage("确定要删除「${task.fileName}」吗？\n已下载的文件也会被删除。")
            .setPositiveButton("删除") { _, _ ->
                mViewModel.deleteTask(task)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    /**
     * 显示错误对话框
     */
    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("提示")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }
}
