package com.example.haojie_huang_myruns4.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.haojie_huang_myruns4.R
import com.example.haojie_huang_myruns4.Util
import com.example.haojie_huang_myruns4.fragment.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.ArrayList

class MainActivity : AppCompatActivity()
{
    //Fragment
    private lateinit var fragmentStart: StartFragment
    private lateinit var fragmentHistory: HistoryFragment
    private lateinit var fragmentSetting: SettingsFragment
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var tab: TabLayout
    private var tabNames = arrayOf("START", "HISTORY", "SETTINGS")
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentStateAdapter: FragmentStateAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Util.checkPermissions(this)

        //Fragment
        fragmentStart = StartFragment()
        fragmentHistory = HistoryFragment()
        fragmentSetting = SettingsFragment()
        fragments = ArrayList()
        fragments.add(fragmentStart)
        fragments.add(fragmentHistory)
        fragments.add(fragmentSetting)

        //Tab&ViewPage
        tab = findViewById(R.id.tab)
        viewPager = findViewById(R.id.viewpager)
        fragmentStateAdapter = FragmentStateAdapter(this, fragments)
        viewPager.adapter = fragmentStateAdapter
        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy()
        {
            tab: TabLayout.Tab, position: Int -> tab.text = tabNames[position]
        }
        tabLayoutMediator = TabLayoutMediator(tab, viewPager, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}