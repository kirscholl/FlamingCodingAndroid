package com.example.pffbrowser.base

import androidx.viewbinding.ViewBinding

/**
 * Author:Knight
 * Time:2021/12/15 16:02
 * Description:BaseView
 */
interface IBaseView<VB : ViewBinding> {

    /**
     * 初始化View
     * 在Activity onCreate调用
     */
    fun VB.initView()

    fun VB.initClickListener()

    /**
     * 订阅LiveData
     * 在Activity onCreate调用
     */
    fun initObserver()

    /**
     * 用于在页面创建时进行请求接口
     * 在Activity onCreate调用
     */
    fun initRequestData()

    /**
     * 用于重新请求接口
     */
    fun reLoadData()

    /**
     * 页面是否重建：
     * fragment被回收重新展示的时候为true，系统环境发生变化activity重新创建时为true
     */
    fun isRecreate(): Boolean
}