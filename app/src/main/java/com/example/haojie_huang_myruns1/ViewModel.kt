package com.example.haojie_huang_myruns1

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModel: ViewModel()
{
    val user_image = MutableLiveData<Bitmap>()
    val user_name = MutableLiveData<String>()
    val user_email = MutableLiveData<String>()
    val user_phone = MutableLiveData<String>()
    val user_gender = MutableLiveData<Int>()
    val user_class = MutableLiveData<String>()
    val user_major = MutableLiveData<String>()
}