package com.example.haojie_huang_myruns2

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat()
{
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val manager: PreferenceManager = preferenceManager
        manager.sharedPreferencesName = getString(R.string.settings_preference_key)
        setPreferencesFromResource(R.xml.settingspreferences, rootKey)
    }
}