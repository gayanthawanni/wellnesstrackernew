package com.example.wellnesstracker.ui.habits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnesstracker.R
import com.example.wellnesstracker.model.Habit
import java.text.SimpleDateFormat
import java.util.*

class HabitsListAdapter(
    private val onToggleComplete: (Habit) -> Unit,
    private val onEdit: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsListAdapter.Holder>() {

    private var items: MutableList<Habit> = mutableListOf()

    private val habitIcons = mapOf(
        "Drink Water" to "💧",
        "Meditate" to "🧘",
        "Walk 10k Steps" to "🚶",
        "Sleep 8 Hours" to "💤",
        "Exercise" to "💪",
        "Read" to "📚",
        "Yoga" to "🧘‍♀️"
    )

    private val habitColors = mapOf(
        "Drink Water" to R.color.habit_blue,
        "Meditate" to R.color.habit_purple,
        "Walk 10k Steps" to R.color.habit_green,
        "Sleep 8 Hours" to R.color.habit_teal,
        "Exercise" to R.color.habit_orange,
        "Read" to R.color.habit_pink,
        "Yoga" to R.color.habit_yellow
    )

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitIcon: TextView = itemView.findViewById(R.id.habitIcon)
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textTarget: TextView = itemView.findViewById(R.id.textTarget)
        val completionToggle: CheckBox = itemView.findViewById(R.id.completionToggle)
        val completionStatus: TextView = itemView.findViewById(R.id.completionStatus)
        val textStreak: TextView = itemView.findViewById(R.id.textStreak)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        // Set habit icon and color
        val icon = habitIcons[item.title] ?: "✅"
        val colorRes = habitColors[item.title] ?: R.color.habit_blue

        holder.habitIcon.text = icon
        holder.habitIcon.setBackgroundColor(
            ContextCompat.getColor(context, R.color.habit_blue_light)
        )
        holder.habitIcon.setTextColor(
            ContextCompat.getColor(context, colorRes)
        )

        // Set habit info
        holder.textTitle.text = item.title
        holder.textTarget.text = "Target: ${getTargetText(item)}"

        // Set completion status
        val todayKey = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val isCompleted = item.isCompletedToday(todayKey)

        holder.completionToggle.isChecked = isCompleted
        updateCompletionStatus(holder, isCompleted)
        
        // Set streak count
        val streakText = if (item.streak == 1) "Streak: 1 day" else "Streak: ${item.streak} days"
        holder.textStreak.text = streakText

        // Toggle completion - remove and re-add listener to avoid infinite loops
        holder.completionToggle.setOnCheckedChangeListener(null)
        holder.completionToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != isCompleted) {
                onToggleComplete(item)
            }
        }

        // Edit action
        holder.btnEdit.setOnClickListener {
            onEdit(item)
        }

        // Delete action
        holder.btnDelete.setOnClickListener {
            onDelete(item)
        }
    }

    private fun getTargetText(habit: Habit): String {
        return when {
            habit.title.contains("Water", ignoreCase = true) -> "8 glasses"
            habit.title.contains("Meditate", ignoreCase = true) -> "1 session"
            habit.title.contains("Steps", ignoreCase = true) -> "10,000 steps"
            habit.title.contains("Sleep", ignoreCase = true) -> "8 hours"
            habit.title.contains("Exercise", ignoreCase = true) -> "30 minutes"
            habit.title.contains("Read", ignoreCase = true) -> "30 minutes"
            habit.title.contains("Yoga", ignoreCase = true) -> "20 minutes"
            else -> "Daily"
        }
    }

    private fun updateCompletionStatus(holder: Holder, isCompleted: Boolean) {
        val context = holder.itemView.context
        
        if (isCompleted) {
            // Update text and background
            holder.completionStatus.text = "Completed"
            holder.completionStatus.setBackgroundResource(R.drawable.status_completed)
            holder.completionStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
            
            // Apply visual feedback for completed state
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.completed_background))
            holder.textTitle.setTextColor(ContextCompat.getColor(context, R.color.completed_text))
            
            // Add a subtle animation
            holder.itemView.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        } else {
            // Update text and background
            holder.completionStatus.text = "Not Completed"
            holder.completionStatus.setBackgroundResource(R.drawable.status_not_completed)
            holder.completionStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
            
            // Reset visual feedback
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            holder.textTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<Habit>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
    
    fun getItemAt(position: Int): Habit {
        return items[position]
    }
}