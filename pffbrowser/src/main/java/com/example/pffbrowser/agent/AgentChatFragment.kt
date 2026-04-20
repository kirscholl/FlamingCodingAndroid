package com.example.pffbrowser.agent

import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentAgentChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgentChatFragment : BaseFragment<PbFragmentAgentChatBinding, AgentChatViewModel>() {

    private val chatAdapter = ChatAdapter()
    override fun PbFragmentAgentChatBinding.initView() {
        // 设置RecyclerView
        rvChatMessages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        // 输入框回车发送
        etInputAgent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    override fun PbFragmentAgentChatBinding.setOnClickListener() {
        btnSendAgent.setOnClickListener {
            sendMessage()
        }
    }

    override fun initViewObserver() {
        // 观察消息列表变化
        mViewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages) {
                // 滚动到最新消息
                if (messages.isNotEmpty()) {
                    mViewBinding.rvChatMessages.smoothScrollToPosition(messages.size - 1)
                }
            }
        }

        // 观察流式输出状态
        mViewModel.isStreaming.observe(viewLifecycleOwner) { isStreaming ->
            mViewBinding.btnSendAgent.isEnabled = !isStreaming
            mViewBinding.etInputAgent.isEnabled = !isStreaming
        }
    }

    private fun sendMessage() {
        val input = mViewBinding.etInputAgent.text.toString().trim()
        if (input.isNotEmpty()) {
            mViewModel.sendMessage(input)
            mViewBinding.etInputAgent.text?.clear()
        }
    }
}
