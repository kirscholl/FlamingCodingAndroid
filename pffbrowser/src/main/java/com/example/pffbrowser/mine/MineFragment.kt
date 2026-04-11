package com.example.pffbrowser.mine

import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentMineBinding
import com.example.pffbrowser.ext.safeNavigateWithAnim

class MineFragment : BaseFragment<PbFragmentMineBinding, MineViewModel>() {

    companion object {
        const val TAG = "MineFragment"
    }

    override fun PbFragmentMineBinding.setOnClickListener() {
        mViewBinding.btnBrowserHistory.setOnClickListener {
            findNavController().safeNavigateWithAnim(
                R.id.mineFragment,
                R.id.pb_action_minefragment_to_browserhistoryfragment
            )
        }

        mViewBinding.btnDownloadManager.setOnClickListener {
            findNavController().safeNavigateWithAnim(
                R.id.mineFragment,
                R.id.pb_action_minefragment_to_downloadlistfragment
            )
        }
    }
}
