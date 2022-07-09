package com.example.haojie_huang_myruns3.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.haojie_huang_myruns3.R
import com.example.haojie_huang_myruns3.ViewModel.InformationViewModel
import com.example.haojie_huang_myruns3.ViewModel.InformationViewModelFactory
import com.example.haojie_huang_myruns3.database.InformationDatabase
import com.example.haojie_huang_myruns3.database.InformationRepository
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.properties.Delegates

class InformationActivity: AppCompatActivity()
{
    private lateinit var viewModel: InformationViewModel
    var id by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information_display)

        //Replace toolbar
        val toolbar: Toolbar = findViewById(R.id.exerciseToolbar)
        setSupportActionBar(toolbar)

        val inputType = findViewById<EditText>(R.id.inputType)
        val activityType = findViewById<EditText>(R.id.activityType)
        val dateTime = findViewById<EditText>(R.id.dateTime)
        val duration = findViewById<EditText>(R.id.duration)
        val distance = findViewById<EditText>(R.id.distance)
        val calories = findViewById<EditText>(R.id.calories)
        val heartRate = findViewById<EditText>(R.id.heartRate)

        val database = InformationDatabase.getInstance(this)
        val databaseDao = database.informationDatabaseDao
        val repository = InformationRepository(databaseDao)
        val factory = InformationViewModelFactory(repository)
        viewModel = ViewModelProvider(this , factory).get(InformationViewModel::class.java)

        val extras = intent.extras
        if(extras != null)
        {
            id = extras.getLong("ENTRY_ID")
            viewModel.allEntriesLiveData.observe(this)
            {
                val entry = it.find {temp -> temp.id == id}
                val sharedPref = getSharedPreferences("PREFERENCES_KEY", Context.MODE_PRIVATE)
                val units = sharedPref.getString("UNITS_KEY", "")

                if (entry != null)
                {
                    var distanceVM = if (units == "Miles")
                    {
                        getNoMoreThanTwoDigits(entry.distance)
                    }
                    else
                    {
                        getNoMoreThanTwoDigits(entry.distance * 1.609344F)
                    }
                    val min = (entry.duration / 60).toInt()
                    val sec = (entry.duration % 60).toInt()
                    if (min == 0)
                    {
                        duration.setText("${sec}secs")
                    }
                    else
                    {
                        duration.setText("${min}mins ${sec}secs")
                    }

                    inputType.setText(inputTypeChecked(entry.inputType))
                    activityType.setText(activityTypeChecked(entry.activityType))
                    dateTime.setText(entry.dateTime)
                    distance.setText("${distanceVM} ${units}")
                    calories.setText("${getNoMoreThanTwoDigits(entry.calories)} cals")
                    heartRate.setText("${getNoMoreThanTwoDigits(entry.heartRate)} bpm")
                }
            }
        }
    }

    fun onDeleteClicked(view: View)
    {
        viewModel.delete(id)
        finish()
    }

    private fun getNoMoreThanTwoDigits(number: Double): String
    {
        val format = DecimalFormat("0.##")
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }

    private fun inputTypeChecked(id: Int): String
    {
        var temp = ""
        when(id)
        {
            0 -> {temp = "Manual Entry"}
            1 -> {temp = "GPS"}
            2 -> {temp = "Automatic"}
        }
        return temp
    }

    private fun activityTypeChecked(id: Int): String
    {
        var temp = ""
        when(id)
        {
            0 ->{temp = "Running"}
            1 ->{temp = "Walking"}
            2 ->{temp = "Standing"}
            3 ->{temp = "Cycling"}
            4 ->{temp = "Hiking"}
            5 ->{temp = "Downhill Skiing"}
            6 ->{temp = "Cross-Country Skiing"}
            7 ->{temp = "Snowboarding"}
            8 ->{temp = "Skating"}
            9 ->{temp = "Swimming"}
            10 ->{temp = "Mountain Biking"}
            11 ->{temp = "Wheelchair"}
            12 ->{temp = "Elliptical"}
            13 ->{temp = "Other"}
        }
        return temp
    }
}