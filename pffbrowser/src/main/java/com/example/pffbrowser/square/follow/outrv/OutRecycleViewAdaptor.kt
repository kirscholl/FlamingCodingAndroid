package com.example.pffbrowser.square.follow.outrv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pffbrowser.R

class OutRecycleViewAdaptor : RecyclerView.Adapter<OutRecycleViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OutRecycleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pb_follow_out_rv_item, parent, false)
        val viewHolder = OutRecycleViewHolder(itemView)
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: OutRecycleViewHolder,
        position: Int
    ) {

    }

    override fun getItemCount(): Int {
        return 0
    }
}