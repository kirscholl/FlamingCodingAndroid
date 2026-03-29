package com.example.pffbrowser.square.follow.outrv

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pffbrowser.R

class OutRecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val img: ImageView = itemView.findViewById(R.id.img_out_rv)
    val tv: TextView = itemView.findViewById(R.id.tv_out_rv)
}