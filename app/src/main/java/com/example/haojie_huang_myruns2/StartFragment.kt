package com.example.haojie_huang_myruns2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment

class StartFragment : Fragment()
{
    private lateinit var inputTypeSpinner : Spinner

    private lateinit var startButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_start, container, false)

        inputTypeSpinner = view.findViewById(R.id.inputTypeSpinner)

        //Button
        startButton = view.findViewById(R.id.startButton)
        startButton.setOnClickListener()
        {
            val inputType = inputTypeSpinner.getSelectedItem().toString()
            var intent: Intent? = null

            println("debug:1")
            if (inputType == "Manual Entry")
            {
                println("debug:2")
                intent = Intent(context, ManualActivity().javaClass)
            }
            else if (inputType == "GPS")
            {
                println("debug:3")
                intent = Intent(context, MapActivity::class.java)
            }
            else if (inputType == "Automatic")
            {
                intent = Intent(context, MapActivity::class.java)
            }
            startActivity(intent)
        }
        return view
    }
}