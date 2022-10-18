package com.silkysys.pos.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.silkysys.pos.data.model.auth.user.get.Data

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(data: Data?)

    @Query("SELECT * FROM user")
    fun getUser(): LiveData<Data>

    @Query("DELETE FROM user")
    suspend fun deleteUser()
}