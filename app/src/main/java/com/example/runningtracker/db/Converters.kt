package com.example.runningtracker.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Converters {

    @TypeConverter
    fun uriFromString(value: String?): Uri?{
        return Uri.parse(value)
    }
    @TypeConverter
    fun stringFromUri(value: Uri?): String {
        return value.toString()
    }

    @TypeConverter
    fun dateFromString(value: String?): Date? {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return value?.let { simpleDateFormat.parse(it) }
    }

    @TypeConverter
    fun dateToString(value: Date): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return simpleDateFormat.format(value)
    }
}