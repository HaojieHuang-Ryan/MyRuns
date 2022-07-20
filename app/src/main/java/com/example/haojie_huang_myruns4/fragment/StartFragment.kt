package com.example.haojie_huang_myruns4.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.haojie_huang_myruns4.R
import com.example.haojie_huang_myruns4.ViewModel.InformationViewModel
import com.example.haojie_huang_myruns4.ViewModel.InformationViewModelFactory
import com.example.haojie_huang_myruns4.activity.ManualActivity
import com.example.haojie_huang_myruns4.activity.MapActivity
import com.example.haojie_huang_myruns4.database.Information
import com.example.haojie_huang_myruns4.database.InformationDatabase
import com.example.haojie_huang_myruns4.database.InformationRepository
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class StartFragment : Fragment()
{
    private lateinit var startActivity: ActivityResultLauncher<Intent>

    private lateinit var inputTypeSpinner : Spinner
    private lateinit var activityTypeSpinner : Spinner
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        //Database setup
        val database = InformationDatabase.getInstance(requireActivity())
        val databaseDao = database.informationDatabaseDao
        val repository = InformationRepository(databaseDao)
        val factory = InformationViewModelFactory(repository)
        val viewModel = ViewModelProvider(this , factory).get(InformationViewModel::class.java)

        startActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK)
            {
                val intent = it.data!!
                val information = Information()
                val extras = intent.extras
                if(extras != null)
                {
                    information.inputType = extras.getInt("INPUT_TYPE")
                    information.activityType = extras.getInt("ACTIVITY_TYPE")
                    information.dateTime = extras.getString("DATE_TIME").toString()
                    information.duration = extras.getDouble("DURATION")
                    information.distance = extras.getDouble("DISTANCE")
                    information.calories = extras.getDouble("CALORIES")
                    information.heartRate = extras.getDouble("HEART_RATE")
                    information.comment = extras.getString("COMMENT").toString()
                    information.avgPace = extras.getDouble("AVERAGE_PACE")
                    information.avgSpeed = extras.getDouble("AVERAGE_SPEED")
                    information.climb = extras.getDouble("CLIMB")
                    if (extras.getInt("INPUT_TYPE") != 0)
                    {
                        information.locations = toArrayList(extras.getString("LOCATIONS")!!)
                    }
                }
                viewModel.insert(information)
                val count = viewModel.getCount() + 1
                Toast.makeText(requireActivity(), "Entry #$count saved.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(requireActivity(), "Entry discarded", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_start, container, false)

        inputTypeSpinner = view.findViewById(R.id.inputTypeSpinner)
        activityTypeSpinner = view.findViewById(R.id.activityTypeSpinner)
        startButton = view.findViewById(R.id.startButton)

        startButton.setOnClickListener()
        {
            val inputType = inputTypeSpinner.selectedItemPosition
            val activityType = activityTypeSpinner.selectedItemPosition
            var intent: Intent? = null

            when(inputType)
            {
                0 -> {intent = Intent(context, ManualActivity::class.java)}
                1 -> {intent = Intent(context, MapActivity::class.java)}
                2 -> {intent = Intent(context, MapActivity::class.java)}
            }

            /*
             * Pass the input type and activity type to manual activity
             * For convenience, package and pass the information to the database
             */
            if (intent != null)
            {
                intent.putExtra("INPUT_TYPE", inputType)
                intent.putExtra("ACTIVITY_TYPE", activityType)
            }

            startActivity.launch(intent)
        }
        return view
    }

    fun toArrayList(json: String): ArrayList<LatLng>
    {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        val array: ArrayList<LatLng> = gson.fromJson(json, listType)
        return array
    }
}