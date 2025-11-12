package com.example.wellnesstracker.model

// Model representing a mood journal entry with a timestamp and emoji codepoint
// stored as a String for easy JSON serialization.
data class MoodEntry(
    val id: Long,
    val timestamp: Long,
    var emoji: String,
    var note: String? = null,
    var moodType: MoodType = MoodType.HAPPY,
    var moodLabel: String = "Happy"
) {
    enum class MoodType(val emoji: String, val label: String, val colorRes: Int) {
        HAPPY("😊", "Happy", com.example.wellnesstracker.R.color.mood_happy),
        EXCITED("🤩", "Excited", com.example.wellnesstracker.R.color.mood_excited),
        LOVE("🥰", "Love", com.example.wellnesstracker.R.color.mood_love),
        CALM("😌", "Calm", com.example.wellnesstracker.R.color.mood_calm),
        SAD("😢", "Sad", com.example.wellnesstracker.R.color.mood_sad),
        ANGRY("😠", "Angry", com.example.wellnesstracker.R.color.mood_angry),
        TIRED("😴", "Tired", com.example.wellnesstracker.R.color.mood_tired),
        STRESSED("😰", "Stressed", com.example.wellnesstracker.R.color.mood_stressed);

        companion object {
            fun fromEmoji(emoji: String): MoodType {
                return values().find { it.emoji == emoji } ?: HAPPY
            }

            fun fromLabel(label: String): MoodType {
                return values().find { it.label.equals(label, ignoreCase = true) } ?: HAPPY
            }
        }
    }

    fun getMoodColor(): Int = moodType.colorRes
}
