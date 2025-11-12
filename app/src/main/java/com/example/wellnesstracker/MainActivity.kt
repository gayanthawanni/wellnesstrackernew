package com.example.wellnesstracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wellnesstracker.databinding.ActivityMainBinding
import com.example.wellnesstracker.ui.habits.HabitsFragment
import com.example.wellnesstracker.ui.hydration.HydrationFragment
import com.example.wellnesstracker.ui.mood.MoodFragment
import com.example.wellnesstracker.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> switchFragment(HabitsFragment())
                R.id.nav_mood -> switchFragment(MoodFragment())
                R.id.nav_hydration -> switchFragment(HydrationFragment())
                R.id.nav_settings -> switchFragment(SettingsFragment())
            }
            true
        }

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_habits
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}