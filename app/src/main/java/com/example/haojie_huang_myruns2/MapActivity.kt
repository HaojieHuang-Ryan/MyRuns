package com.example.haojie_huang_myruns2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MapActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
    }

    fun mapOnSaveClicked(view: View)
    {
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun mapOnCancelClicked(view: View)
    {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        finish()
    }
}