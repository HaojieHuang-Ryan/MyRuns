package com.example.haojie_huang_myruns5.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.haojie_huang_myruns5.R
import com.example.haojie_huang_myruns5.TrackService
import com.example.haojie_huang_myruns5.viewModel.InformationViewModel
import com.example.haojie_huang_myruns5.viewModel.InformationViewModelFactory
import com.example.haojie_huang_myruns5.viewModel.MapViewModel
import com.example.haojie_huang_myruns5.database.InformationDatabase
import com.example.haojie_huang_myruns5.database.InformationRepository
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class MapActivity: AppCompatActivity(), OnMapReadyCallback
{

    private lateinit var viewModel: InformationViewModel
    var id by Delegates.notNull<Long>()

    private lateinit var mMap : GoogleMap
    private val PERMISSION_REQUEST_CODE = 0
    private var mapCentered = false
    private lateinit var locationList: ArrayList<LatLng>
    private lateinit var  markerOptions: MarkerOptions
    private lateinit var  polylineOptions: PolylineOptions
    private lateinit var  polylines: ArrayList<Polyline>
    private var lastMarker : Marker? = null

    private lateinit var serviceIntent : Intent
    private lateinit var mapViewModel: MapViewModel
    private var isBind: Boolean = false


    private lateinit var activityTypeTV: TextView
    private lateinit var averageSpeedTV: TextView
    private lateinit var currentSpeedTV: TextView
    private lateinit var climbTV: TextView
    private lateinit var caloriesTV: TextView
    private lateinit var distanceTV: TextView
    private lateinit var units: String

    //Get from bundle and save to intent
    private var inputType: Int = 0
    private var activityType: Int = 0
    private var dateTime: String = ""
    private var duration: Double = 0.0
    private var distance: Double = 0.0
    private var avgPace: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var calories: Double = 0.0
    private var climb: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        serviceIntent = Intent(this, TrackService::class.java)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        locationList = ArrayList()
        activityTypeTV = findViewById(R.id.map_activity_type)
        averageSpeedTV = findViewById(R.id.map_average_speed)
        currentSpeedTV = findViewById(R.id.map_current_speed)
        climbTV = findViewById(R.id.map_climb)
        caloriesTV = findViewById(R.id.map_calorie)
        distanceTV = findViewById(R.id.map_distance)
        val sharedPref = getSharedPreferences("PREFERENCES_KEY", Context.MODE_PRIVATE)
        units = sharedPref.getString("UNITS_KEY", "").toString()


        val temp_format = SimpleDateFormat("HH:mm:ss MM/dd/yyyy")
        dateTime = temp_format.format(Calendar.getInstance().time)

        val displayToolbar: Toolbar = findViewById(R.id.displaceToolbar)
        val defaultToolbar: Toolbar = findViewById(R.id.defaultToolbar)

        val extras = intent.extras
        if(extras != null)
        {
            inputType = extras.getInt("INPUT_TYPE")
            activityType = extras.getInt("ACTIVITY_TYPE")

            if(extras.containsKey("ENTRY_ID"))
            {
                setSupportActionBar(displayToolbar)
                val temp_button: LinearLayout = findViewById(R.id.map_buttons)
                temp_button.visibility = View.GONE
                defaultToolbar.visibility = View.GONE

                when(activityType)
                {
                    0 ->{activityTypeTV.text = "Type: Running"}
                    1 ->{activityTypeTV.text = "Type: Walking"}
                    2 ->{activityTypeTV.text = "Type: Standing"}
                    3 ->{activityTypeTV.text = "Type: Cycling"}
                    4 ->{activityTypeTV.text = "Type: Hiking"}
                    5 ->{activityTypeTV.text = "Type: Downhill Skiing"}
                    6 ->{activityTypeTV.text = "Type: Cross-Country Skiing"}
                    7 ->{activityTypeTV.text = "Type: Snowboarding"}
                    8 ->{activityTypeTV.text = "Type: Skating"}
                    9 ->{activityTypeTV.text = "Type: Swimming"}
                    10 ->{activityTypeTV.text = "Type: Mountain Biking"}
                    11 ->{activityTypeTV.text = "Type: Wheelchair"}
                    12 ->{activityTypeTV.text = "Type: Elliptical"}
                    13 ->{activityTypeTV.text = "Type: Other"}
                    14 ->{activityTypeTV.text = "Type: Unknown"}
                }

                val database = InformationDatabase.getInstance(this)
                val databaseDao = database.informationDatabaseDao
                val repository = InformationRepository(databaseDao)
                val factory = InformationViewModelFactory(repository)
                viewModel = ViewModelProvider(this , factory).get(InformationViewModel::class.java)
            }
            else
            {
                displayToolbar.visibility = View.INVISIBLE
                setSupportActionBar(defaultToolbar)
            }

            if (inputType == 2)
            {
                activityTypeTV.text = "Type: Unknown"
                activityType = 14
                mapViewModel.classifiedActivityType.observe(this)
                {
                    activityTypeTV.text = "Type: $it"
                    when(it.toString())
                    {
                        "Running" ->{activityType = 0}
                        "Walking" ->{activityType = 1}
                        "Standing" ->{activityType = 2}
                        "Other" ->{activityType = 13}
                    }
                }
            }
            else
            {
                when(activityType)
                {
                    0 ->{activityTypeTV.text = "Type: Running"}
                    1 ->{activityTypeTV.text = "Type: Walking"}
                    2 ->{activityTypeTV.text = "Type: Standing"}
                    3 ->{activityTypeTV.text = "Type: Cycling"}
                    4 ->{activityTypeTV.text = "Type: Hiking"}
                    5 ->{activityTypeTV.text = "Type: Downhill Skiing"}
                    6 ->{activityTypeTV.text = "Type: Cross-Country Skiing"}
                    7 ->{activityTypeTV.text = "Type: Snowboarding"}
                    8 ->{activityTypeTV.text = "Type: Skating"}
                    9 ->{activityTypeTV.text = "Type: Swimming"}
                    10 ->{activityTypeTV.text = "Type: Mountain Biking"}
                    11 ->{activityTypeTV.text = "Type: Wheelchair"}
                    12 ->{activityTypeTV.text = "Type: Elliptical"}
                    13 ->{activityTypeTV.text = "Type: Other"}
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap)
    {
        mMap = googleMap
        markerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.RED)
        polylines = ArrayList()

        val extras = intent.extras
        if(extras != null)
        {
            if(extras.containsKey("ENTRY_ID"))
            {
                id = extras.getLong("ENTRY_ID")
                viewModel.allEntriesLiveData.observe(this)
                {
                    val entry = it.find { temp -> temp.id == id }
                    if (entry != null)
                    {
                        val temp_locationList = entry.locations
                        val temp_duration = entry.duration
                        val temp_distance = entry.distance
                        val temp_avgPace = entry.avgPace
                        val temp_avgSpeed = entry.avgSpeed
                        val temp_calories = entry.calories
                        val temp_climb = entry.climb
                        val temp_bundle = Bundle()
                        temp_bundle.putString("LOCATION", fromArrayList(temp_locationList))
                        temp_bundle.putDouble("DURATION", temp_duration)
                        temp_bundle.putDouble("DISTANCE", temp_distance)
                        temp_bundle.putDouble("CURRENT_SPEED", temp_avgPace)
                        temp_bundle.putDouble("AVERAGE_SPEED", temp_avgSpeed)
                        temp_bundle.putDouble("CALORIES", temp_calories)
                        temp_bundle.putDouble("CLIMB", temp_climb)
                        mapOut(temp_bundle)
                    }
                }
            }
            else
            {
                checkPermission()
                mapViewModel.bundle.observe(this)
                {
                    mapOut(it)
                }
            }
        }

    }

    private fun checkPermission()
    {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
        }
        else
        {
            startTrackService()
        }
    }

    private fun startTrackService()
    {
        applicationContext.startService(serviceIntent)
        if (!isBind)
        {
            applicationContext.bindService(serviceIntent, mapViewModel, Context.BIND_AUTO_CREATE)
            isBind = true
        }
    }

    private fun mapOut(bundle: Bundle)
    {
        //Get from service
        locationList = toArrayList(bundle.getString("LOCATION")!!)
        if (locationList.isEmpty())
        {
            mMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))
        }
        else
        {
            duration = bundle.getDouble("DURATION")
            distance = bundle.getDouble("DISTANCE")
            avgPace = bundle.getDouble("CURRENT_SPEED")
            avgSpeed = bundle.getDouble("AVERAGE_SPEED")
            calories = bundle.getDouble("CALORIES")
            climb = bundle.getDouble("CLIMB")
            changeMapStatus()

            val extras = intent.extras
            if(extras != null)
            {
                if (extras.containsKey("ENTRY_ID"))
                {
                    currentSpeedTV.text = "Cur speed: n/a"
                }
            }

            polylineOptions = PolylineOptions()
            polylineOptions.addAll(locationList)
            mMap.addPolyline(polylineOptions)

            if (!mapCentered)
            {
                val latLng = locationList.first()
                markerOptions.position(latLng).title("Start location")
                mMap.addMarker(markerOptions)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20f)
                mMap.animateCamera(cameraUpdate)
                mapCentered = true
            }

            val latLng = locationList.last()
            lastMarker?.remove()
            markerOptions.position(latLng).title("End location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            lastMarker = mMap.addMarker(markerOptions)
        }
    }

    private fun changeMapStatus()
    {
        if (units == "Miles")
        {
            averageSpeedTV.text = "Avg speed: ${String.format("%.1f" ,avgSpeed)} m/h"
            currentSpeedTV.text = "Cur speed: ${String.format("%.1f" ,avgPace)} m/h"
            climbTV.text = "Climb: ${String.format("%.1f" ,climb)} Miles"
            caloriesTV.text = "Calorie: ${String.format("%.1f" ,calories)}"
            distanceTV.text = "Distance: ${String.format("%.1f" ,distance)} Miles"
        }
        else
        {
            averageSpeedTV.text = "Avg speed: ${String.format("%.1f" ,(avgSpeed * 1.609344F))} km/h"
            currentSpeedTV.text = "Cur speed: ${String.format("%.1f" ,avgPace * 1.609344F)} km/h"
            climbTV.text = "Climb: ${String.format("%.1f" ,climb * 1.609344F)} Kilometers"
            caloriesTV.text = "Calorie: ${String.format("%.1f" ,calories)}"
            distanceTV.text = "Distance: ${String.format("%.1f" ,distance * 1.609344F)} Kilometers"
        }
    }

    private fun toArrayList(json : String ): ArrayList<LatLng>
    {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        val array: ArrayList<LatLng> = gson.fromJson(json, listType)
        return array
    }

    private fun fromArrayList(array : ArrayList<LatLng>): String
    {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        val json: String = gson.toJson(array, listType)
        return json
    }

    fun mapOnSaveClicked(view: View)
    {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putInt("INPUT_TYPE", inputType)
        bundle.putInt("ACTIVITY_TYPE", activityType)
        bundle.putString("DATE_TIME", dateTime)
        bundle.putDouble("DURATION", duration)
        bundle.putDouble("DISTANCE", distance)
        bundle.putDouble("CALORIES", calories)
        bundle.putDouble("HEART_RATE", 0.0)
        bundle.putString("COMMENT", "")
        bundle.putDouble("AVERAGE_PACE", avgPace)
        bundle.putDouble("AVERAGE_SPEED", avgSpeed)
        bundle.putDouble("CLIMB", climb)
        bundle.putString("LOCATIONS", fromArrayList(locationList))

        if (avgPace.isNaN())
        {
            bundle.putDouble("AVERAGE_PACE", 0.0)
        }
        if (avgSpeed.isNaN())
        {
            bundle.putDouble("AVERAGE_SPEED", 0.0)
        }

        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    fun mapOnCancelClicked(view: View)
    {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun onDeleteClicked(view: View)
    {
        viewModel.delete(id)
        finish()
    }

    override fun finish()
    {
        super.finish()
        if (isBind)
        {
            applicationContext.unbindService(mapViewModel)
            stopService(serviceIntent)
            isBind = false
        }
    }
}