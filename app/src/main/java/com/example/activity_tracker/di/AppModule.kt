package com.example.activity_tracker.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.activity_tracker.db.RunningDao
import com.example.activity_tracker.db.RunningDatabase
import com.example.activity_tracker.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun privateSharedPreferences(app: Application): SharedPreferences {

        return app.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideRunningDatabase(app: Application): RunningDatabase {
        return Room.databaseBuilder(
            app,
            RunningDatabase::class.java,
            Constants.RUNNING_DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideRunningDao(db: RunningDatabase): RunningDao {
        return db.getRunningDao()
    }

    @Singleton
    @Provides
    fun simpleDateFormat():SimpleDateFormat{
        return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    }
}