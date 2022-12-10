package com.example.mvvmrunningtrackerapp.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runningtracker.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun privateSharedPreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    /*@Provides
    @Singleton
    fun provideName(sharedPref: SharedPreferences): String = sharedPref.getString(
        Constants.KEY_NAME, "") ?: ""

    @Provides
    @Singleton
    fun provideWeight(sharedPref: SharedPreferences): Float = sharedPref.getFloat(
        Constants.KEY_WEIGHT, 80f)

    @Provides
    @Singleton
    fun provideFirstTimeToggle(sharedPref: SharedPreferences): Boolean = sharedPref.getBoolean(
        Constants.KEY_FIRST_TIME_TOGGLE, true)

    @Provides
    @Singleton
    fun provideRunningDatabase(app: Application): RunningDataBase = Room.databaseBuilder(
        app,
        RunningDataBase::class.java,
        Constants.RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDataBase): RunDao = db.getRunDao()*/
}