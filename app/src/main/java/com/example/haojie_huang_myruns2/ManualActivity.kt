package com.example.haojie_huang_myruns2

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ManualActivity : AppCompatActivity()
{
    private val MANUAL_ENTRY = arrayOf(
        "Date",
        "Time",
        "Duration",
        "Distance",
        "Calories",
        "Heart Rate",
        "Comment"
    )

    private lateinit var manualList: ListView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        manualList = findViewById(R.id.manualList)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, MANUAL_ENTRY)
        manualList.adapter = arrayAdapter
    }

    fun manualOnSaveClicked(view: View)
    {
        Toast.makeText(this, "Entry saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun manualOnCancelClicked(view: View)
    {
        Toast.makeText(this, "Entry discarded", Toast.LENGTH_SHORT).show()
        finish()
    }
}