package com.example.wellnesstracker.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WaterEntry(
    val id: Long,
    val timestamp: Long,
    val amountMl: Int,
    val cupType: CupType = CupType.SMALL_GLASS
) {
    enum class CupType(val displayName: String, val volumeMl: Int, val iconRes: Int) {
        SMALL_GLASS(
            "Small glass",
            200,
            com.example.wellnesstracker.R.drawable.ic_launcher_foreground
        ),
        MEDIUM_GLASS(
            "Medium glass",
            240,
            com.example.wellnesstracker.R.drawable.ic_launcher_foreground
        ),
        LARGE_GLASS(
            "Large glass",
            350,
            com.example.wellnesstracker.R.drawable.ic_launcher_foreground
        ),
        WATER_BOTTLE(
            "Water bottle",
            500,
            com.example.wellnesstracker.R.drawable.ic_launcher_foreground
        );

        companion object {
            fun getDefault() = MEDIUM_GLASS
        }
    }

    fun getDateKey(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(timestamp))
    }
}

data class DailyWaterGoal(
    val date: String, // yyyyMMdd format
    val targetMl: Int = 2000,
    var currentMl: Int = 0,
    val entries: MutableList<WaterEntry> = mutableListOf()
) {
    fun getProgressPercentage(): Int {
        return if (targetMl > 0) {
            minOf(100, (currentMl * 100) / targetMl)
        } else 0
    }

    fun getRemainingMl(): Int {
        return maxOf(0, targetMl - currentMl)
    }

    fun getProgressLevels(): List<Boolean> {
        // Return 8 levels (each 250ml for 2000ml target)
        val levelSize = targetMl / 8
        return (0 until 8).map { level ->
            currentMl >= (level + 1) * levelSize
        }
    }
}