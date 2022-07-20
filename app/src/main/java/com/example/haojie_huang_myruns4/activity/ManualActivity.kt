package com.example.haojie_huang_myruns4.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.haojie_huang_myruns4.R
import com.example.haojie_huang_myruns4.ViewModel.ManualViewModel
import com.google.android.gms.maps.model.LatLng
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class ManualActivity : AppCompatActivity()
{
    private var inputType: Int = 0
    private var activityType: Int = 0

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

    //Date and time
    private lateinit var dateDialog: DatePickerDialog
    private lateinit var timeDialog: TimePickerDialog
    private var pickedCalendar = Calendar.getInstance()

    //The rest of data
    private var duration: Double = 0.0
    private var distance: Double = 0.0
    private var calories: Double = 0.0
    private var heartrate: Double = 0.0
    private var comment: String = ""

    private val manualVM: ManualViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual)

        val extras = intent.extras
        if(extras != null)
        {
            inputType = extras.getInt("INPUT_TYPE")
            activityType = extras.getInt("ACTIVITY_TYPE")
        }

        //ViewModel
        manualVM.VMpicked_calendar.observe(this)
        {
            pickedCalendar = it
        }
        manualVM.VMduration.observe(this)
        {
            duration = it
        }
        manualVM.VMdistance.observe(this)
        {
            distance = it
        }
        manualVM.VMcalories.observe(this)
        {
            calories = it
        }
        manualVM.VMheartrate.observe(this)
        {
            heartrate = it
        }
        manualVM.VMcomment.observe(this)
        {
            comment = it
        }

        manualList = findViewById(R.id.manualList)
        manualList.setOnItemClickListener { parent: AdapterView<*>, textView: View, position: Int, id: Long ->
            when (position)
            {
                0 -> {
                    createDateDialog()
                }
                1 -> {
                    createTimeDialog()
                }
                2 -> {
                    createdDialog(duration.toString(), MANUAL_ENTRY[position], "")
                }
                3 -> {
                    createdDialog(distance.toString(), MANUAL_ENTRY[position], "")
                }
                4 -> {
                    createdDialog(calories.toString(), MANUAL_ENTRY[position], "")
                }
                5 -> {
                    createdDialog(heartrate.toString(), MANUAL_ENTRY[position], "")
                }
                6 -> {
                    createdDialog(comment, MANUAL_ENTRY[position], "How did it go? Notes here.")
                }
            }
        }
    }

    private fun createDateDialog()
    {
        dateDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                pickedCalendar.set(Calendar.YEAR, year)
                pickedCalendar.set(Calendar.MONTH, month)
                pickedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                manualVM.VMpicked_calendar.value = pickedCalendar },
            pickedCalendar.get(Calendar.YEAR), pickedCalendar.get(Calendar.MONTH), pickedCalendar.get(Calendar.DAY_OF_MONTH))
        dateDialog.show()
    }

    private fun createTimeDialog()
    {
        timeDialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                pickedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                pickedCalendar.set(Calendar.MINUTE, minute)
                manualVM.VMpicked_calendar.value = pickedCalendar},
            pickedCalendar.get(Calendar.HOUR_OF_DAY), pickedCalendar.get(Calendar.MINUTE), true)
        pickedCalendar.set(Calendar.SECOND, 0)
        timeDialog.show()
    }

    private fun createdDialog(input: String, title: String, hint: String)
    {
        val myDialog = CustomDialog()
        val bundle = Bundle()
        if (input == "0.0")
        {
            val temp = ""
            bundle.putString("input", temp)
        }
        else
        {
            bundle.putString("input", input)
        }
        bundle.putString("title", title)
        bundle.putString("hint", hint)
        bundle.putInt(CustomDialog.DIALOG_KEY, CustomDialog.TEST_DIALOG)
        myDialog.arguments = bundle
        myDialog.show(supportFragmentManager, "my dialog")
    }

    fun manualOnSaveClicked(view: View)
    {
        val temp_format = SimpleDateFormat("HH:mm:ss MM/dd/yyyy")
        val datetime = temp_format.format(pickedCalendar.time)
        val bundle = Bundle()
        when(inputType)
        {
            0 -> {bundle.putInt("INPUT_TYPE", 0)}
            1 -> {bundle.putInt("INPUT_TYPE", 1)}
            2 -> {bundle.putInt("INPUT_TYPE", 2)}
        }
        when(activityType)
        {
            0 -> {bundle.putInt("ACTIVITY_TYPE", 0)}
            1 -> {bundle.putInt("ACTIVITY_TYPE", 1)}
            2 -> {bundle.putInt("ACTIVITY_TYPE", 2)}
            3 -> {bundle.putInt("ACTIVITY_TYPE", 3)}
            4 -> {bundle.putInt("ACTIVITY_TYPE", 4)}
            5 -> {bundle.putInt("ACTIVITY_TYPE", 5)}
            6 -> {bundle.putInt("ACTIVITY_TYPE", 6)}
            7 -> {bundle.putInt("ACTIVITY_TYPE", 7)}
            8 -> {bundle.putInt("ACTIVITY_TYPE", 8)}
            9 -> {bundle.putInt("ACTIVITY_TYPE", 9)}
            10 -> {bundle.putInt("ACTIVITY_TYPE", 10)}
            11 -> {bundle.putInt("ACTIVITY_TYPE", 11)}
            12 -> {bundle.putInt("ACTIVITY_TYPE", 12)}
            13 -> {bundle.putInt("ACTIVITY_TYPE", 13)}
        }
        bundle.putString("DATE_TIME", datetime)
        bundle.putDouble("DURATION", duration)
        bundle.putDouble("DISTANCE", distance)
        bundle.putDouble("CALORIES", calories)
        bundle.putDouble("HEART_RATE", heartrate)
        bundle.putString("COMMENT", comment)
        bundle.putDouble("AVERAGE_PACE", 0.0)
        bundle.putDouble("AVERAGE_SPEED", 0.0)
        bundle.putDouble("CLIMB", 0.0)
        bundle.putString("LOCATIONS", "")
        val intent = Intent()
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun manualOnCancelClicked(view: View)
    {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}