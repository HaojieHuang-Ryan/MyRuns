package com.example.haojie_huang_myruns3.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.haojie_huang_myruns3.R

class SettingsFragment : PreferenceFragmentCompat()
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        val manager: PreferenceManager = preferenceManager
        manager.sharedPreferencesName = "PREFERENCES_KEY"
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}