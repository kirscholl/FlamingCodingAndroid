package com.example.pffbrowser.mine

import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentMineBinding

class MineFragment : BaseFragment<PbFragmentMineBinding, MineViewModel>() {

    companion object {
        const val TAG = "MineFragment"
    }

    override fun PbFragmentMineBinding.setOnClickListener() {
        mViewBinding.btnBrowserHistory.setOnClickListener {
            findNavController().navigate(
                R.id.pb_action_searchfragment_to_searchresultfragment
            )
        }
    }
}