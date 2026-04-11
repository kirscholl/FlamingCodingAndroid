package com.example.pffbrowser.mine.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pffbrowser.databinding.PbItemBrowserHistoryBinding
import com.example.pffbrowser.databinding.PbItemDateHeaderBinding
import com.example.pffbrowser.mine.data.BrowserHistory

class BrowserHistoryAdapter(
    private val onDelete: (BrowserHistory) -> Unit
) : ListAdapter<HistoryListItem, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ENTRY = 1
        // 删除按钮露出的宽度（px），在 bind 时根据 llDeleteBg 实际宽度动态计算
        private const val SWIPE_THRESHOLD_DP = 80f

        private val DIFF = object : DiffUtil.ItemCallback<HistoryListItem>() {
            override fun areItemsTheSame(a: HistoryListItem, b: HistoryListItem) = when {
                a is HistoryListItem.Header && b is HistoryListItem.Header -> a.dateLabel == b.dateLabel
                a is HistoryListItem.Entry && b is HistoryListItem.Entry -> a.history.id == b.history.id
                else -> false
            }
            override fun areContentsTheSame(a: HistoryListItem, b: HistoryListItem) = a == b
        }
    }

    // 当前展开的 ViewHolder，用于在展开新 item 时收起旧的
    private var expandedHolder: EntryViewHolder? = null

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is HistoryListItem.Header -> TYPE_HEADER
        is HistoryListItem.Entry -> TYPE_ENTRY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(PbItemDateHeaderBinding.inflate(inflater, parent, false))
        } else {
            EntryViewHolder(PbItemBrowserHistoryBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HistoryListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is HistoryListItem.Entry -> (holder as EntryViewHolder).bind(item.history)
        }
    }

    // 列表刷新时收起所有展开项
    override fun submitList(list: List<HistoryListItem>?) {
        collapseExpanded()
        super.submitList(list)
    }

    fun collapseExpanded() {
        expandedHolder?.collapse()
        expandedHolder = null
    }

    // -------------------------------------------------------------------------

    inner class HeaderViewHolder(private val binding: PbItemDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryListItem.Header) {
            binding.root.text = item.dateLabel
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class EntryViewHolder(private val binding: PbItemBrowserHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var deleteWidth = 0f   // 删除按钮露出宽度（px）
        private var downX = 0f
        private var downY = 0f
        private var isOpen = false

        init {
            // 用固定 dp 值作为露出宽度，避免读取 llDeleteBg 全宽
            binding.llDeleteBg.post {
                val density = binding.root.context.resources.displayMetrics.density
                deleteWidth = 80 * density  // 80dp
            }

            binding.clForeground.setOnTouchListener { _, event ->
                handleTouch(event)
            }

            binding.llDeleteBg.setOnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_ID.toInt()) return@setOnClickListener
                val item = getItem(pos)
                if (item is HistoryListItem.Entry) {
                    collapse()
                    expandedHolder = null
                    onDelete(item.history)
                }
            }
        }

        fun bind(history: BrowserHistory) {
            // 复用时重置状态
            binding.clForeground.translationX = 0f
            isOpen = false

            binding.tvTitle.text = history.title
            binding.tvUrl.text = history.url
            binding.tvTime.text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(history.visitTime))
            binding.ivFavicon.setImageResource(com.example.pffbrowser.R.drawable.pb_ic_history_empty)
        }

        private fun handleTouch(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                    return true   // 必须消费 DOWN，防止事件穿透到 llDeleteBg
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - downX
                    val dy = event.rawY - downY
                    // 垂直滑动幅度大于水平时不处理（让 RecyclerView 滚动）
                    if (Math.abs(dy) > Math.abs(dx)) return false
                    if (Math.abs(dx) < 10) return true

                    // 展开新 item 时收起旧的
                    if (expandedHolder != null && expandedHolder != this) {
                        expandedHolder?.collapse()
                        expandedHolder = null
                    }

                    val target = when {
                        dx < 0 -> dx.coerceAtLeast(-deleteWidth)   // 左滑，最多露出删除按钮
                        else   -> if (isOpen) (dx - deleteWidth).coerceAtMost(0f) else 0f
                    }
                    binding.clForeground.translationX = target
                    return true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val dx = event.rawX - downX
                    val dy = event.rawY - downY
                    val isClick = Math.abs(dx) < 10 && Math.abs(dy) < 10
                    if (isClick) {
                        // 点击前景时，如果已展开则收起
                        if (isOpen) {
                            collapse()
                            expandedHolder = null
                        }
                        return true
                    }
                    // 判断是否应该展开或收起
                    val currentTx = binding.clForeground.translationX
                    if (currentTx < -(deleteWidth / 2)) {
                        expand()
                    } else {
                        collapse()
                        expandedHolder = null
                    }
                    return true
                }
            }
            return false
        }

        fun expand() {
            isOpen = true
            expandedHolder = this
            ObjectAnimator.ofFloat(binding.clForeground, "translationX", -deleteWidth)
                .apply { duration = 200 }.start()
        }

        fun collapse() {
            isOpen = false
            ObjectAnimator.ofFloat(binding.clForeground, "translationX", 0f)
                .apply { duration = 200 }.start()
        }
    }
}
