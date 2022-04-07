package com.riyaz.cipheraz.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi

class GetUriMetadata(applicationContext: Context, uri: Uri) {
    val contentResolver = applicationContext.contentResolver
    @RequiresApi(Build.VERSION_CODES.O)
    val cursor: Cursor? = contentResolver.query(uri, null, null, null)
    fun getName(): String{
        cursor?.use {
            if(it.moveToFirst()){
                val name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME)?:0)
                return name
            }
        }
        return "Couldn't get name!"
    }
}