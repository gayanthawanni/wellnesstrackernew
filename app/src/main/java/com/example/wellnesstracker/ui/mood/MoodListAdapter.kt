package com.example.wellnesstracker.ui.mood

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnesstracker.databinding.ItemMoodBinding
import com.example.wellnesstracker.model.MoodEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodListAdapter(private var items: MutableList<MoodEntry>) :
    RecyclerView.Adapter<MoodListAdapter.Holder>() {

    inner class Holder(val binding: ItemMoodBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context

        // Format time
        val now = System.currentTimeMillis()
        val diff = now - item.timestamp
        val timeText = when {
            diff < 60 * 60 * 1000 -> { // Less than 1 hour
                val minutes = (diff / (60 * 1000)).toInt()
                if (minutes < 1) "Just now" else "${minutes}m ago"
            }

            diff < 24 * 60 * 60 * 1000 -> { // Less than 24 hours
                val hours = (diff / (60 * 60 * 1000)).toInt()
                "${hours}h ago"
            }

            else -> {
                val df = SimpleDateFormat("MMM d", Locale.getDefault())
                df.format(Date(item.timestamp))
            }
        }

        // Set background color based on mood type
        val backgroundColor = ContextCompat.getColor(context, item.getMoodColor())
        holder.binding.moodContainer.setBackgroundColor(backgroundColor)

        // Bind data
        holder.binding.textEmoji.text = item.emoji
        holder.binding.textMoodLabel.text = item.moodLabel
        holder.binding.textNote.text = item.note ?: "No notes"
        holder.binding.textTime.text = timeText

        // Hide note if empty
        if (item.note.isNullOrBlank()) {
            holder.binding.textNote.text = ""
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: MutableList<MoodEntry>) {
        items = newItems
        notifyDataSetChanged()
    }
}


