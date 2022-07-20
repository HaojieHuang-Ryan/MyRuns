package com.example.haojie_huang_myruns4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.haojie_huang_myruns4.activity.MapActivity
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TrackService: Service(), LocationListener
{
    //About notification
    private lateinit var notificationManager: NotificationManager
    private val CHANNEL_ID = "notification channel"
    private val NOTIFICATION_ID = 315

    //About location
    private lateinit var locationManager: LocationManager
    private lateinit var locationList : ArrayList<LatLng>
    private lateinit var lastLocation : Location

    //About message
    private var duration: Double = 0.0
    private var distance: Double = 0.0
    private var curSpeed: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var calories: Double = 0.0
    private var climb: Double = 0.0

    private var startTime : Long = 0L
    private var lastTime : Long = 0L

    //About bind
    private lateinit var  myBinder: MyBinder
    private var msgHandler: Handler? = null

    companion object
    {
        val MSG_INT_VALUE = 0
    }

    override fun onCreate()
    {
        super.onCreate()
        showNotification()

        myBinder = MyBinder()

        locationList = ArrayList()
        initLocationManager()

        startTime = System.currentTimeMillis()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        return START_NOT_STICKY
    }

    private fun showNotification()
    {
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.logo)
        notificationBuilder.setContentTitle("MyRuns")
        notificationBuilder.setContentText("Recording your path now")

        //Set notification clicked situation
        val intent = Intent(this, MapActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(pendingIntent)


        ///Build notification and check notification version
        val notification = notificationBuilder.build()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26)
        {
            val notificationChannel = NotificationChannel(CHANNEL_ID, "channel name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun initLocationManager()
    {
        try
        {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider : String? = locationManager.getBestProvider(criteria, true)
            if(provider != null)
            {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null)
                    onLocationChanged(location)
                locationManager.requestLocationUpdates(provider, 0, 0f, this)
            }
        }
        catch (e: SecurityException)
        {
            println("debug:LocationManager")
        }
    }

    override fun onLocationChanged(location: Location)
    {
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)
        locationList.add(latLng)

        if (::lastLocation.isInitialized)
        {
            locationList.add(latLng)

            val currentTime = System.currentTimeMillis()
            duration = ((currentTime - startTime) / 1000).toDouble()

            distance += lastLocation.distanceTo(location) / 1609.344F

            curSpeed = ((lastLocation.distanceTo(location) / 1609.344F) / ((currentTime - startTime) / 3600)).toDouble()

            avgSpeed = distance / (duration / 3600)

            calories = (distance * 120)

            climb = (lastLocation.altitude - location.altitude) / 1000
        }

        lastLocation = location
        lastTime = System.currentTimeMillis()

        //Send message
        sendMessage()
    }

    override fun onBind(p0: Intent?): IBinder?
    {
        return myBinder
    }

    inner class MyBinder : Binder()
    {
        fun setmsgHandler(msgHandler: Handler)
        {
            this@TrackService.msgHandler = msgHandler
        }
    }

    override fun onUnbind(intent: Intent?): Boolean
    {
        msgHandler = null
        return true
    }

    private fun sendMessage()
    {
        try
        {
            if(msgHandler != null)
            {
                val bundle = Bundle()
                bundle.putDouble("DURATION", duration)
                bundle.putDouble("DISTANCE", distance)
                bundle.putDouble("CURRENT_SPEED", curSpeed)
                bundle.putDouble("AVERAGE_SPEED", avgSpeed)
                bundle.putDouble("CALORIES", calories)
                bundle.putDouble("CLIMB", climb)
                bundle.putString("LOCATION", fromArrayList(locationList))

                val message = msgHandler!!.obtainMessage()
                message.data = bundle
                message.what = MSG_INT_VALUE
                msgHandler!!.sendMessage(message)
            }
        }
        catch (t: Throwable)
        {
            println("debug: send message failed. $t")
        }
    }

    private fun fromArrayList(array : ArrayList<LatLng>): String
    {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        val json: String = gson.toJson(array, listType)
        return json
    }

    private fun cleanupTasks()
    {
        msgHandler = null
        notificationManager.cancel(NOTIFICATION_ID)
        if (locationManager != null)
        {
            locationManager.removeUpdates(this)
        }
        locationList.clear()
    }

    override fun onTaskRemoved(rootIntent: Intent?)
    {
        super.onTaskRemoved(rootIntent)
        cleanupTasks()
        stopSelf()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        cleanupTasks()
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}