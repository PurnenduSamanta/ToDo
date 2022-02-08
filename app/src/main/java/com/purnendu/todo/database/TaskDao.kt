package com.purnendu.todo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(taskModel: TaskModel)

    @Query("Select * from Task where isFinished =-1 ")
    fun getTask():LiveData<List<TaskModel>>

    @Query("Select * from Task where isFinished =1 ")
    fun getFinishedTask():LiveData<List<TaskModel>>


    @Query("Update Task Set isFinished=1 where id=:uId")
    suspend fun finishedTask(uId:Long)

    @Query("Update Task Set isFinished=-1 where id=:uId")
    suspend fun unFinishedTask(uId:Long)

    @Query("Delete from Task where id=:uId")
    suspend fun deletedTask(uId:Long)
}