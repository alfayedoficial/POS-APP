package com.silkysys.pos.data.di

import android.app.Application
import androidx.room.Room
import com.silkysys.pos.data.local.CartDatabase
import com.silkysys.pos.data.local.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, CartDatabase::class.java, "cart_db")
            .fallbackToDestructiveMigration()
            .build()


    @Provides
    @Singleton
    fun provideUserDatabase(app: Application) =
        Room.databaseBuilder(app, UserDatabase::class.java, "user_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserDao(db: UserDatabase) = db.getUserDao()


    @Provides
    @Singleton
    fun provideProductsDao(db: CartDatabase) = db.getCartDao()
}