package com.example.wellnesstracker.data

import android.content.Context
import android.content.SharedPreferences
import com.example.wellnesstracker.model.Habit
import com.example.wellnesstracker.model.MoodEntry
import com.example.wellnesstracker.model.WaterEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Repository encapsulating all SharedPreferences persistence.
class PrefsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("wellness_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val keyHabits = "key_habits"
    private val keyMoods = "key_moods"
    private val keyWaterEntries = "key_water_entries"
    private val keyHydrationEnabled = "key_hydration_enabled"
    private val keyHydrationIntervalMinutes = "key_hydration_interval"

    fun getHabits(): MutableList<Habit> {
        val json = prefs.getString(keyHabits, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Habit>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveHabits(list: List<Habit>) {
        prefs.edit().putString(keyHabits, gson.toJson(list)).apply()
    }

    fun getMoods(): MutableList<MoodEntry> {
        val json = prefs.getString(keyMoods, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveMoods(list: List<MoodEntry>) {
        prefs.edit().putString(keyMoods, gson.toJson(list)).apply()
    }

    fun getWaterEntries(): MutableList<WaterEntry> {
        val json = prefs.getString(keyWaterEntries, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<WaterEntry>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveWaterEntries(list: List<WaterEntry>) {
        prefs.edit().putString(keyWaterEntries, gson.toJson(list)).apply()
    }

    fun isHydrationEnabled(): Boolean = prefs.getBoolean(keyHydrationEnabled, false)

    fun setHydrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(keyHydrationEnabled, enabled).apply()
    }

    fun getHydrationIntervalMinutes(): Int = prefs.getInt(keyHydrationIntervalMinutes, 120)

    fun setHydrationIntervalMinutes(minutes: Int) {
        prefs.edit().putInt(keyHydrationIntervalMinutes, minutes).apply()
    }
}


