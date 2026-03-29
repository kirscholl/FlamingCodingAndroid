package com.example.pffbrowser.mine

import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentMineBinding

class MineFragment : BaseFragment<PbFragmentMineBinding, MineViewModel>() {

    companion object {
        const val TAG = "MineFragment"
    }

    override fun PbFragmentMineBinding.setOnClickListener() {
        mViewBinding.btnBrowserHistory.setOnClickListener {

        }

        mViewBinding.btnDownloadManager.setOnClickListener {

        }
    }
}
