package com.example.haojie_huang_myruns5.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.haojie_huang_myruns5.R

class SettingsFragment : PreferenceFragmentCompat()
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        val manager: PreferenceManager = preferenceManager
        manager.sharedPreferencesName = "PREFERENCES_KEY"
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}