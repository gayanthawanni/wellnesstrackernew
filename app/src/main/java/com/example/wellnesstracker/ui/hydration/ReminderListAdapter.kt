package com.example.wellnesstracker.ui.hydration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnesstracker.databinding.ItemReminderBinding

class ReminderListAdapter(
    private var reminders: MutableList<String>,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<ReminderListAdapter.Holder>() {

    inner class Holder(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val reminder = reminders[position]

        holder.binding.reminderTime.text = reminder

        holder.binding.deleteBtn.setOnClickListener {
            onDelete(reminder)
        }
    }

    override fun getItemCount(): Int = reminders.size

    fun update(newReminders: List<String>) {
        reminders = newReminders.toMutableList()
        notifyDataSetChanged()
    }

    fun getReminders(): List<String> = reminders
}