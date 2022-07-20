package com.example.haojie_huang_myruns4.ViewModel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haojie_huang_myruns4.TrackService

class MapViewModel: ViewModel(), ServiceConnection
{
    private var messageHandler: MessageHandler = MessageHandler(Looper.getMainLooper())
    private var _bundle = MutableLiveData<Bundle>()
    val bundle: LiveData<Bundle> get()
    {
        return _bundle
    }

    override fun onServiceConnected(name: ComponentName, iBinder: IBinder)
    {
        val tempBinder = iBinder as TrackService.MyBinder
        tempBinder.setmsgHandler(messageHandler)
    }

    override fun onServiceDisconnected(name: ComponentName?)
    {

    }

    inner class MessageHandler(looper: Looper) : Handler(looper)
    {
        override fun handleMessage(msg: Message)
        {
            if (msg.what == TrackService.MSG_INT_VALUE)
            {
                _bundle.value = msg.data
            }
        }
    }
}