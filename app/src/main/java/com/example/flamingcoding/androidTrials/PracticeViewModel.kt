package com.example.flamingcoding.androidTrials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap

class PracticeViewModel(countReserved: Int) : ViewModel() {

    // 如果你需要在子线程中给LiveData设置数据，一定要调用postValue()方法


    //利用LiveData将数据变化通知给Activity
//    public var counter = MutableLiveData<Int>()
//
//    init {
//        counter.value = countReserved
//    }
//
//    fun plusPne() {
//        val count = counter.value ?: 0
//        counter.value = count + 1
//    }
//
//    fun clear() {
//        counter.value = 0
//    }

    //     // 比较推荐的做法是，永远只暴露不可变的LiveData给外部。这样在非ViewModel中就只能观察LiveData的数据变化，而不能给LiveData设置数据
    val counter: LiveData<Int>
        get() = _counter

    private val _counter = MutableLiveData<Int>()

    init {
        _counter.value = countReserved
    }

    fun plusOne() {
        val count = _counter.value ?: 0
        _counter.value = count + 1
    }

    fun clear() {
        _counter.value = 0
    }

    //##############################################################################################
    // 如果MainActivity中明确只会显示部分字段，那么这个时候还将整个数据类型的LiveData暴露给外部，就不那么合适。
    // 而map()方法就是专门用于解决这种问题的，它可以将User类型的LiveData自由地转型成任意其他类型的LiveData
    inner class User(userId: String, userName: String, userAge: Int) {
        var id: String = userId
        var name: String = userName
    }

    private val userLiveData = MutableLiveData<User>()

    val userName: LiveData<String> = userLiveData.map { user ->
        user.name
    }
    //##############################################################################################

    //##############################################################################################
    inner class Repository {
        fun getUser(userId: String): LiveData<User> {
            val liveData = MutableLiveData<User>()
            liveData.value = User(userId, userId, 0)
            return liveData
        }
    }

    // 如果ViewModel中的某个LiveData对象是调用另外的方法获取的，那么我们就可以借助switchMap()方法，
    // 将这个LiveData对象转换成另外一个可观察的LiveData对象
    private val userIdLiveData = MutableLiveData<String>()
    private val repository = Repository()
    val user: LiveData<User> = userIdLiveData.switchMap { userId ->
        repository.getUser(userId)
    }
    //##############################################################################################
}