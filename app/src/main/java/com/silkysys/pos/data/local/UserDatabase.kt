package com.silkysys.pos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.silkysys.pos.data.model.auth.user.get.Data


@Database(
    entities = [Data::class],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}