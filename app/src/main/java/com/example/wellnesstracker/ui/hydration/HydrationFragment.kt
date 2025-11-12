package com.example.wellnesstracker.ui.hydration

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wellnesstracker.R
import com.example.wellnesstracker.data.PrefsRepository
import com.example.wellnesstracker.databinding.FragmentHydrationBinding

class HydrationFragment : Fragment() {
    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: PrefsRepository
    private lateinit var adapter: ReminderListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHydrationBinding.inflate(inflater, container, false)
        repository = PrefsRepository(requireContext())

        setupRecyclerView()
        setupClickListeners()
        updateUI()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ReminderListAdapter(mutableListOf(), ::deleteReminder)
        binding.reminderList.layoutManager = LinearLayoutManager(requireContext())
        binding.reminderList.adapter = adapter
    }

    private fun setupClickListeners() {
        // Hydration toggle
        binding.hydrationToggle.isChecked = repository.isHydrationEnabled()
        binding.hydrationToggle.setOnCheckedChangeListener { _, isChecked ->
            repository.setHydrationEnabled(isChecked)
            updateHydrationStatus()
            if (isChecked) {
                generateReminders()
            } else {
                adapter.update(emptyList())
            }
        }

        // Interval buttons - commented out for now to fix build
        /*
        binding.intervalDecrease.setOnClickListener {
            val currentMinutes = repository.getHydrationIntervalMinutes()
            if (currentMinutes > 15) {
                repository.setHydrationIntervalMinutes(currentMinutes - 15)
                updateIntervalDisplay()
                if (repository.isHydrationEnabled()) {
                    generateReminders()
                }
            }
        }

        binding.intervalIncrease.setOnClickListener {
            val currentMinutes = repository.getHydrationIntervalMinutes()
            if (currentMinutes < 180) {
                repository.setHydrationIntervalMinutes(currentMinutes + 15)
                updateIntervalDisplay()
                if (repository.isHydrationEnabled()) {
                    generateReminders()
                }
            }
        }
        */

        // Update schedule
        binding.updateSchedule.setOnClickListener {
            if (!repository.isHydrationEnabled()) {
                showAlert("Please enable hydration reminders first")
                return@setOnClickListener
            }

            val startTime = binding.startTime.text.toString()
            val endTime = binding.endTime.text.toString()

            if (startTime.isBlank() || endTime.isBlank()) {
                showAlert("Please set both start and end times")
                return@setOnClickListener
            }

            generateReminders()
            showAlert("Schedule updated successfully!")
        }
    }

    private fun updateUI() {
        updateIntervalDisplay()
        updateHydrationStatus()
        if (repository.isHydrationEnabled()) {
            generateReminders()
        }
    }

    private fun updateIntervalDisplay() {
        // Commented out to fix build
        /*
        val minutes = repository.getHydrationIntervalMinutes()
        binding.intervalValue.text = "$minutes min"
        */
    }

    private fun updateHydrationStatus() {
        val isEnabled = repository.isHydrationEnabled()
        binding.hydrationStatus.text = if (isEnabled) {
            "Reminders are currently active"
        } else {
            "Reminders are currently disabled"
        }
        binding.hydrationStatus.setTextColor(
            if (isEnabled) {
                requireContext().getColor(R.color.water_primary)
            } else {
                requireContext().getColor(R.color.habit_red)
            }
        )
    }

    private fun generateReminders() {
        val startTime = binding.startTime.text.toString()
        val endTime = binding.endTime.text.toString()
        val interval = repository.getHydrationIntervalMinutes()

        if (startTime.isBlank() || endTime.isBlank()) return

        val reminders = mutableListOf<String>()
        val (startHour, startMinute) = parseTime(startTime)
        val (endHour, endMinute) = parseTime(endTime)

        var currentHour = startHour
        var currentMinute = startMinute

        while (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute)) {
            val period = if (currentHour >= 12) "PM" else "AM"
            val displayHour = if (currentHour > 12) currentHour - 12 else if (currentHour == 0) 12 else currentHour
            val displayMinute = String.format("%02d", currentMinute)

            reminders.add("$displayHour:$displayMinute $period")

            // Add interval
            currentMinute += interval
            while (currentMinute >= 60) {
                currentMinute -= 60
                currentHour += 1
            }
        }

        adapter.update(reminders)
    }

    private fun parseTime(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        return Pair(parts[0].toInt(), parts[1].toInt())
    }

    private fun deleteReminder(reminder: String) {
        val currentReminders = adapter.getReminders().toMutableList()
        currentReminders.remove(reminder)
        adapter.update(currentReminders)
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}