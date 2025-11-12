package com.example.wellnesstracker.model

// Simple model representing a wellness habit. We mark a habit as completed by
// storing the last date (yyyyMMdd) it was completed. UI treats completion as
// true when lastCompletedDate == today.
data class Habit(
    val id: Long,
    var title: String,
    var color: Int,
    var lastCompletedDate: String? = null,
    var completionHistory: MutableList<String> = mutableListOf(), // List of yyyyMMdd dates
    var streak: Int = 0,
    var targetDaysPerWeek: Int = 7 // Daily by default
) {
    fun getCompletionPercentage(): Int {
        // Calculate percentage based on last 30 days
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000)
        val completedDays = completionHistory.size
        val targetDays = 30 * targetDaysPerWeek / 7
        return if (targetDays > 0) {
            minOf(100, (completedDays * 100) / targetDays)
        } else 0
    }

    fun isCompletedToday(todayKey: String): Boolean {
        return lastCompletedDate == todayKey
    }

    fun getWeeklyProgress(): List<Boolean> {
        // Return last 7 days completion status
        val today = System.currentTimeMillis()
        val weekProgress = mutableListOf<Boolean>()

        for (i in 6 downTo 0) {
            val dayTimestamp = today - (i * 24 * 60 * 60 * 1000)
            val dayKey = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
                .format(java.util.Date(dayTimestamp))
            weekProgress.add(completionHistory.contains(dayKey))
        }

        return weekProgress
    }
}
