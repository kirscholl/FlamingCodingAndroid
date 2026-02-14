package com.example.flamingcoding.androidTrials

import androidx.room.Entity
import androidx.room.PrimaryKey

// ● Entity。用于定义封装实际数据的实体类，每个实体类都会在数据库中有一张对应的表，并且表中的列是根据实体类中的字段自动生成的。
// ● Dao。Dao是数据访问对象的意思，通常会在这里对数据库的各项操作进行封装，在实际编程的时候，逻辑层就不需要和底层数据库打交道了，直接和Dao层进行交互即可。
// ● Database。用于定义数据库中的关键信息，包括数据库的版本号、包含哪些实体类以及提供Dao层的访问实例。
@Entity
data class RoomPracticeData(
    var url: String,
    var linkName: String,
    var icon: String,
    var browserTime: Int,
    var browserDayTime: Int? = 0
) {
    @PrimaryKey(autoGenerate = true)
    var urlId: Long = 0
}