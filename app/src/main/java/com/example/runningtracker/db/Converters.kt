package com.example.runningtracker.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Converters {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
    }

    @TypeConverter
    fun dateFromString(value: String): Date? {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return simpleDateFormat.parse(value)
    }

    @TypeConverter
    fun dateToString(value: Date): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return simpleDateFormat.format(value)
    }
}