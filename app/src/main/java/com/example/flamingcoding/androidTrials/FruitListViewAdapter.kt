package com.example.flamingcoding.androidTrials

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.flamingcoding.R

class FruitListViewAdapter(activity: Activity, val resourceId: Int, data: List<Fruit>) :
    ArrayAdapter<Fruit>(activity, resourceId, data) {

    // 每次在getView()方法中仍然会调用View的findViewById()方法来获取一次控件的实例。可以借助一个ViewHolder来对这部分性能进行优化
    // 创建一个ViewHolder对象，并将控件的实例存放在ViewHolder里，然后调用View的setTag()方法，将ViewHolder对象存储在View中。
    // 当convertView不为null的时候，则调用View的getTag()方法，把ViewHolder重新取出。这样所有控件的实例都缓存在了ViewHolder里，
    // 就没有必要每次都通过findViewById()方法来获取控件实例了
    inner class ViewHolder(val fruitImage: ImageView, val fruitName: TextView)

    // 在FruitAdapter的getView()方法中，每次都将布局重新加载了一遍
    // getView()中的convertView参数，这个参数用于将之前加载好的布局进行缓存，以便之后进行重用，借助这个参数来进行性能优化
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resourceId, parent, false)
            val fruitImage: ImageView = view.findViewById(R.id.fruitImage)
            val fruitName: TextView = view.findViewById(R.id.fruitName)
            viewHolder = ViewHolder(fruitImage, fruitName)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val fruit = getItem(position) // 获取当前项的Fruit实例
        if (fruit != null) {
            viewHolder.fruitImage.setImageResource(fruit.imageId)
            viewHolder.fruitName.text = fruit.name
        }
        return view
    }

}