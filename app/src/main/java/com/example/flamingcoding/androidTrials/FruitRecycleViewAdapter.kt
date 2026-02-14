package com.example.flamingcoding.androidTrials

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.flamingcoding.R

class FruitRecycleViewAdapter(val fruitList: List<Fruit>) :
    RecyclerView.Adapter<FruitRecycleViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fruitImage: ImageView = view.findViewById(R.id.fruitImage)
        val fruitName: TextView = view.findViewById(R.id.fruitName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.fruit_recycle_horizon_item, parent, false)
//        return ViewHolder(view)
        Log.d(
            "onCreateViewHolder",
            "+++++++++++++++++onCreateViewHolder+++++++++++++++++ viewType：$parent"
        )
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fruit_recycle_horizon_item, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fruit = fruitList[position]
        Log.d("onBindViewHolder", "onBindViewHolder 中的position：$position")
        holder.fruitImage.setImageResource(fruit.imageId)
        holder.fruitName.text = fruit.name
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val fruit = fruitList[position]
            Toast.makeText(
                holder.itemView.context, "you clicked view ${fruit.name}, position is : $position",
                Toast.LENGTH_SHORT
            ).show()
        }
        holder.fruitImage.setOnClickListener {
            val position = holder.adapterPosition
            val fruit = fruitList[position]
            Toast.makeText(
                holder.itemView.context, "you clicked image ${fruit.name}, position is : $position",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount() = fruitList.size

    // class FruitAdapter(val fruitList: List<Fruit>) :
    //        RecyclerView.Adapter<FruitAdapter.ViewHolder>() {
    //    ...
    //    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    //        val view = LayoutInflater.from(parent.context)
    //        .inflate(R.layout.fruit_item, parent, false)
    //        val viewHolder = ViewHolder(view)
    //        viewHolder.itemView.setOnClickListener {
    //            val position = viewHolder.adapterPosition
    //            val fruit = fruitList[position]
    //            Toast.makeText(parent.context, "you clicked view ${fruit.name}",
    //                Toast.LENGTH_SHORT).show()
    //        }
    //        viewHolder.fruitImage.setOnClickListener {
    //            val position = viewHolder.adapterPosition
    //            val fruit = fruitList[position]
    //            Toast.makeText(parent.context, "you clicked image ${fruit.name}",
    //                Toast.LENGTH_SHORT).show()
    //        }
    //        return viewHolder
    //    }
    //    ...
    //}
}