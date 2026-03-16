package com.example.pffbrowser.square

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pffbrowser.square.find.FindFragment
import com.example.pffbrowser.square.follow.FollowFragment
import com.example.pffbrowser.square.location.LocationFragment

class SquareViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    companion object {
        const val PAGES_NUM = 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FollowFragment()
            }

            1 -> {
                FindFragment()
            }

            else -> LocationFragment()
        }
    }

    override fun getItemCount(): Int {
        return PAGES_NUM
    }
}