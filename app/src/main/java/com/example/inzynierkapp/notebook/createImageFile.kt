package com.example.inzynierkapp.notebook

import android.app.Activity
import android.icu.text.SimpleDateFormat
import android.os.Environment
import java.io.File
import java.util.Date
import java.util.Locale

fun createImageFile(context: Activity): File? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        File.createTempFile(
            "IMG_${timeStamp}_",  /* prefiks */
            ".jpg",              /* rozszerzenie */
            storageDir           /* katalog docelowy */
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}