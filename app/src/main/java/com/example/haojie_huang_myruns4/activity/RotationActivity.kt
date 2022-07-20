package com.example.haojie_huang_myruns4.activity

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.haojie_huang_myruns4.R
import java.io.File
import java.io.FileOutputStream

class RotationActivity : AppCompatActivity()
{
    private lateinit var temp_profile_image_file: File
    private lateinit var temp_uri: Uri
    private lateinit var image_view: ImageView
    var rotation_degree: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation)

        temp_profile_image_file = File(getExternalFilesDir(null), "temp_profile_image_rotated.jpg")

        image_view = findViewById(R.id.rotation_picture)
        temp_uri = Uri.parse(intent.getStringExtra("PASS_URI"))
        image_view.setImageBitmap(getBitmap(temp_uri, 0f))
    }

    fun rotationOnSaveClicked(view: View)
    {
        writeBitmap(temp_uri, rotation_degree)
        intent.putExtra("back_data","hello This is a message for backData")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun rotationOnRotateClicked(view: View)
    {
        rotation_degree = (rotation_degree + 90f).mod(360f)
        image_view.setImageBitmap(getBitmap(temp_uri, rotation_degree))
    }

    private fun getBitmap(imgUri: Uri, rotation: Float): Bitmap
    {
        val bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(rotation)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun writeBitmap(imgUri: Uri, rotation: Float): Bitmap
    {
        val fOut = FileOutputStream(temp_profile_image_file)
        val bitmap = getBitmap(imgUri, 0f)
        val matrix = Matrix()
        matrix.setRotate(rotation)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).compress(
            Bitmap.CompressFormat.JPEG,
            70,
            fOut
        )
        fOut.flush()
        fOut.close()
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}