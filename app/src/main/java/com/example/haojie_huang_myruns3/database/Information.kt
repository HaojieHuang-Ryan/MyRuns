package com.example.haojie_huang_myruns3.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "information_table")
data class Information
(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L, //Primary Key

    @ColumnInfo(name = "input_type")
    var inputType: Int = 0, // Manual, GPS or automatic

    @ColumnInfo(name = "activity_type")
    var activityType: Int = 0, // Running, cycling etc

    //TODO: used Calendar type
    @ColumnInfo(name = "date_time")
    var dateTime: String = "", // When does this entry happen

    @ColumnInfo(name = "duration")
    var duration: Double = 0.0, // Exercise duration in seconds

    @ColumnInfo(name = "distance")
    var distance: Double = 0.0, // Distance traveled. Either in meters or feet

    @ColumnInfo(name = "average_pace")
    var avgPace: Double = 0.0, // Average pace

    @ColumnInfo(name = "average_speed")
    var avgSpeed: Double = 0.0, // Average speed

    @ColumnInfo(name = "calories")
    var calories: Double = 0.0, // Calories burnt

    @ColumnInfo(name = "climb")
    var climb: Double = 0.0, // Climb. Either in meters or feet

    @ColumnInfo(name = "heart_rate")
    var heartRate: Double = 0.0,

    @ColumnInfo(name = "comment")
    var comment: String = "",

    //TODO: next assignment task
//    @ColumnInfo(name = "locations")
//    var locations: ArrayList<LatLng> = ArrayList()
)