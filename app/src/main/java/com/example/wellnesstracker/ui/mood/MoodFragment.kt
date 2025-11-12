package com.example.wellnesstracker.ui.mood

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wellnesstracker.databinding.FragmentMoodBinding
import com.example.wellnesstracker.data.PrefsRepository
import com.example.wellnesstracker.model.MoodEntry
import com.example.wellnesstracker.ui.chart.ChartActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodFragment : Fragment() {
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: PrefsRepository
    private lateinit var adapter: MoodListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        repo = PrefsRepository(requireContext())

        setupRecyclerView()
        setupCurrentDate()
        setupMoodSelection()
        setupCurrentMood()
        updateMoodSummary()

        binding.btnAddMood.setOnClickListener { showAddMoodDialog() }
        // Removed binding.btnChart and binding.btnShare click listeners
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = MoodListAdapter(repo.getMoods())
        binding.recyclerMood.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMood.adapter = adapter
    }

    private fun setupCurrentDate() {
        val dateFormat = SimpleDateFormat("d EEEE", Locale.getDefault())
        binding.textDate.text = dateFormat.format(Date())
    }

    private fun setupMoodSelection() {
        val moodButtons = mapOf(
            binding.moodHappy to MoodEntry.MoodType.HAPPY,
            binding.moodExcited to MoodEntry.MoodType.EXCITED,
            binding.moodLove to MoodEntry.MoodType.LOVE,
            binding.moodCalm to MoodEntry.MoodType.CALM,
            binding.moodSad to MoodEntry.MoodType.SAD
        )

        moodButtons.forEach { (layout, moodType) ->
            layout.setOnClickListener {
                addMoodEntry(moodType)
            }
        }
    }

    private fun setupCurrentMood() {
        val moods = repo.getMoods()
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val todayMoods = moods.filter {
            val moodDate =
                SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(it.timestamp))
            moodDate == today
        }

        if (todayMoods.isNotEmpty()) {
            val latestMood = todayMoods.maxByOrNull { it.timestamp }!!
            updateCurrentMoodDisplay(latestMood)
        } else {
            // Show default happy mood
            updateCurrentMoodDisplay(null)
        }
    }

    private fun updateCurrentMoodDisplay(mood: MoodEntry?) {
        if (mood != null) {
            binding.currentMoodEmoji.text = mood.emoji
            binding.currentMoodLabel.text = mood.moodLabel
            binding.currentMoodDescription.text = mood.note ?: "No notes"

            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            binding.currentMoodTime.text = timeFormat.format(Date(mood.timestamp))

            val backgroundColor = ContextCompat.getColor(requireContext(), mood.getMoodColor())
            binding.currentMoodCard.setCardBackgroundColor(backgroundColor)
        } else {
            binding.currentMoodEmoji.text = "😊"
            binding.currentMoodLabel.text = "Ready to track"
            binding.currentMoodDescription.text = "Tap a mood below to get started"
            binding.currentMoodTime.text = ""

            val backgroundColor = ContextCompat.getColor(
                requireContext(),
                com.example.wellnesstracker.R.color.mood_happy
            )
            binding.currentMoodCard.setCardBackgroundColor(backgroundColor)
        }
    }

    private fun updateMoodSummary() {
        val moods = repo.getMoods()
        val today = System.currentTimeMillis()
        val weekStart = today - (7 * 24 * 60 * 60 * 1000)

        val weekMoods = moods.filter { it.timestamp >= weekStart }
        binding.moodCount.text = "${weekMoods.size} entries"
    }

    private fun addMoodEntry(moodType: MoodEntry.MoodType) {
        val input = EditText(requireContext())
        input.hint = "How was your day? (optional)"

        AlertDialog.Builder(requireContext())
            .setTitle("Add ${moodType.label} Mood")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val note = input.text.toString().trim().takeIf { it.isNotEmpty() }
                val list = repo.getMoods()
                val newMood = MoodEntry(
                    id = System.currentTimeMillis(),
                    timestamp = System.currentTimeMillis(),
                    emoji = moodType.emoji,
                    note = note,
                    moodType = moodType,
                    moodLabel = moodType.label
                )
                list.add(newMood)
                repo.saveMoods(list)
                adapter.update(list)
                updateCurrentMoodDisplay(newMood)
                updateMoodSummary()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddMoodDialog() {
        val input = EditText(requireContext())
        input.hint = "Type emoji like 🙂 or short note"
        AlertDialog.Builder(requireContext())
            .setTitle("Add Mood")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val text = input.text.toString().trim()
                val emoji = text.firstOrNull()?.toString() ?: "🙂"
                val note = if (text.length > 1) text.drop(1).trim() else null
                val moodType = MoodEntry.MoodType.fromEmoji(emoji)

                val list = repo.getMoods()
                val newMood = MoodEntry(
                    id = System.currentTimeMillis(),
                    timestamp = System.currentTimeMillis(),
                    emoji = emoji,
                    note = note,
                    moodType = moodType,
                    moodLabel = moodType.label
                )
                list.add(newMood)
                repo.saveMoods(list)
                adapter.update(list)
                updateCurrentMoodDisplay(newMood)
                updateMoodSummary()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun shareSummary() {
        val df = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val text = buildString {
            append("My moods recently:\n")
            repo.getMoods().takeLast(7).forEach {
                append("${df.format(Date(it.timestamp))} ${it.emoji} ${it.note ?: ""}\n")
            }
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, "Share mood summary"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
