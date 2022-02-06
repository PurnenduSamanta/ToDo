package com.purnendu.todo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="Task")
data class TaskModel(
    var title:String,
    var description:String,
    var category:String,
    var date:Long,
    var time:Long,
    var isFinished:Int=-1,
    @PrimaryKey(autoGenerate = true)
    var id:Long=0
)