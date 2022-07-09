package com.example.haojie_huang_myruns2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

object Util {
    fun checkPermissions(activity: Activity?)
    {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 0)
        }
    }

    fun getBitmap(context: Context, imgUri: Uri, rotation : Float): Bitmap
    {
        var bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(rotation)
        var ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }

    fun creatBitmap(context: Context, imageName: String, imgUri: Uri)
    {
        var bitmap = getBitmap(context, imgUri, 90f)
        val matrix = Matrix()
        matrix.setRotate(180f)
        val tempImageFile = File(context.getExternalFilesDir(null), imageName)
        var fileOut = FileOutputStream(tempImageFile)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            .compress(Bitmap.CompressFormat.PNG, 50, fileOut)
        fileOut.flush()
        fileOut.close()
    }
}
