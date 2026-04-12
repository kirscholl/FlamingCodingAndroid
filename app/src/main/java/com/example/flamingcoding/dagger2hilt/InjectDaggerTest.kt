package com.example.flamingcoding.dagger2hilt

import javax.inject.Inject

// Dagger 依赖解耦
// 如果多处使用InjectDaggerTest，要改名或者改方法名，只要在此处修改就行

class InjectDaggerTest @Inject constructor() {

    fun getDaggerValue(): String {
        return "InjectValue"
    }
}
