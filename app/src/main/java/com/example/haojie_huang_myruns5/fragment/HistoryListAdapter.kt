package com.example.haojie_huang_myruns5.fragment

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.haojie_huang_myruns5.R
import com.example.haojie_huang_myruns5.activity.ManualInformationActivity
import com.example.haojie_huang_myruns5.activity.MapActivity
import com.example.haojie_huang_myruns5.database.Information
import java.math.RoundingMode
import java.text.DecimalFormat

class HistoryListAdapter (val context: Context, var entriesList: List<Information>) : BaseAdapter()
{
    override fun getCount(): Int
    {
        return entriesList.size
    }

    override fun getItem(position: Int): Any
    {
        return entriesList[position]
    }

    override fun getItemId(position: Int): Long
    {
        return entriesList[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val view = View.inflate(context, R.layout.layout_history_adapter, null)
        val itemTitle = view.findViewById<TextView>(R.id.entryTitle)
        val itemDetail = view.findViewById<TextView>(R.id.entryDetail)

        val item = entriesList[position]
        var tempInputType = ""
        var tempActivityType = ""

        //About input type and activity type
        when(item.inputType)
        {
            0 -> {tempInputType = "Manual Entry"}
            1 -> {tempInputType = "GPS"}
            2 -> {tempInputType = "Automatic"}
        }
        if (item.inputType == 2)
        {
            when(item.activityType)
            {
                0 -> {tempActivityType = "Running"}
                1 -> {tempActivityType = "Walking"}
                2 -> {tempActivityType = "Standing"}
                13 -> {tempActivityType = "Other"}
                14 -> {tempActivityType = "Unknown"}
            }
        }
        else
        {
            when(item.activityType)
            {
                0 ->{tempActivityType = "Running"}
                1 ->{tempActivityType = "Walking"}
                2 ->{tempActivityType = "Standing"}
                3 ->{tempActivityType = "Cycling"}
                4 ->{tempActivityType = "Hiking"}
                5 ->{tempActivityType = "Downhill Skiing"}
                6 ->{tempActivityType = "Cross-Country Skiing"}
                7 ->{tempActivityType = "Snowboarding"}
                8 ->{tempActivityType = "Skating"}
                9 ->{tempActivityType = "Swimming"}
                10 ->{tempActivityType = "Mountain Biking"}
                11 ->{tempActivityType = "Wheelchair"}
                12 ->{tempActivityType = "Elliptical"}
                13 ->{tempActivityType = "Other"}
            }
        }

        //About distance
        val sharedPref = context.getSharedPreferences("PREFERENCES_KEY", Context.MODE_PRIVATE)
        val units = sharedPref.getString("UNITS_KEY", "")
        var distance = if (units == "Miles")
        {
            getNoMoreThanTwoDigits(item.distance)
        }
        else
        {
            getNoMoreThanTwoDigits(item.distance * 1.609344F)
        }

        //About duration
        val min = (item.duration / 60).toInt()
        val sec = (item.duration % 60).toInt()

        if (min == 0)
        {
            itemDetail.text = "${distance} ${units}, ${sec}secs"
        }
        else
        {
            itemDetail.text = "${distance} ${units}, ${min}mins ${sec}secs"
        }

        itemTitle.text = "${tempInputType}: ${tempActivityType}, ${item.dateTime}"


        view.setOnClickListener()
        {
            var intent: Intent? = null
            intent = if (tempInputType == "Manual Entry")
            {
                Intent(context, ManualInformationActivity::class.java)
            }
            else
            {
                Intent(context, MapActivity::class.java)
            }
            intent.putExtra("ENTRY_ID", item.id)
            intent.putExtra("ACTIVITY_TYPE", item.activityType)
            context.startActivity(intent)
        }
        return view
    }

    fun updateList(replaceList: List<Information>)
    {
        entriesList = replaceList
    }

    private fun getNoMoreThanTwoDigits(number: Double): String
    {
        val format = DecimalFormat("0.##")
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }
}