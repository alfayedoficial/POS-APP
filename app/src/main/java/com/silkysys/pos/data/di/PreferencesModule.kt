package com.silkysys.pos.data.di

import android.content.Context
import android.content.SharedPreferences
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.local.UserPreferences
import com.silkysys.pos.util.CartCounter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {


    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideEditor(@ApplicationContext context: Context): SharedPreferences.Editor {
        return provideSharedPreferences(context).edit()
    }

    @Provides
    @Singleton
    fun provideUserPref(@ApplicationContext context: Context) =
        UserPreferences(provideSharedPreferences(context), provideEditor(context))

    @Provides
    @Singleton
    fun provideCounter(@ApplicationContext context: Context) = CartCounter(provideUserPref(context))
}