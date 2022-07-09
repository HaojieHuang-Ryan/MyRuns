package com.example.haojie_huang_myruns3.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class ManualViewModel: ViewModel()
{
    var VMpicked_calendar = MutableLiveData<Calendar>()
    var VMduration = MutableLiveData<Double>()
    val VMdistance = MutableLiveData<Double>()
    val VMcalories = MutableLiveData<Double>()
    val VMheartrate = MutableLiveData<Double>()
    val VMcomment = MutableLiveData<String>()
}