package com.example.epic.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.epic.fragments.AddMusicsFragment
import com.example.epic.fragments.HomeFragment
import com.example.epic.fragments.SearchFragment
import com.example.epic.fragments.SettingsFragment

class TabPageAdapter(activity: FragmentActivity, private val tabCount: Int): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> SearchFragment()
            2 -> AddMusicsFragment()
            3 -> SettingsFragment()
            else -> HomeFragment()
        }
    }
}