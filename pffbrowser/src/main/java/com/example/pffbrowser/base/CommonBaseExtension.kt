package com.example.pffbrowser.base

import java.lang.reflect.ParameterizedType

/**
 * 获取当前类绑定的泛型ViewModel-clazz
 */
@Suppress("UNCHECKED_CAST")
fun <VM : BaseViewModel> getViewModelClass(obj: Any): Class<VM> {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>
}