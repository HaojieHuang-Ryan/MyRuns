package com.example.haojie_huang_myruns2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentStateAdapter(activity: FragmentActivity, private var list: ArrayList<Fragment>): FragmentStateAdapter (activity)
{
    override fun getItemCount(): Int
    {
        return list.size
    }

    override fun createFragment(position: Int): Fragment
    {
        return list[position]
    }
}