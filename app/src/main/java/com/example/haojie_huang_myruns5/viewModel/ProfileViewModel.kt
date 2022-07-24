package com.example.haojie_huang_myruns5.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel()
{
    val user_image = MutableLiveData<Bitmap>()
}