package com.example.pffbrowser.base

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.util.Objects

object BindingReflex {

    fun <V : ViewBinding> reflexViewBinding(aClass: Class<*>, from: LayoutInflater?): V {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<*>
                try {
                    tClass = actualTypeArguments[i] as Class<*>
                } catch (e: Exception) {
                    continue
                }
                if (ViewBinding::class.java.isAssignableFrom(tClass)) {
                    val inflate = tClass.getMethod("inflate", LayoutInflater::class.java)
                    return inflate.invoke(null, from) as V
                }
            }
            return reflexViewBinding(
                aClass.superclass,
                from
            )
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            throw RuntimeException(e.message)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            throw RuntimeException(e.message)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            throw RuntimeException(e.targetException.message)
        }
    }

}