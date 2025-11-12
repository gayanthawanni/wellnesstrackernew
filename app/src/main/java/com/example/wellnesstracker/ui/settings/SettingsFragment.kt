package com.example.wellnesstracker.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.example.wellnesstracker.databinding.FragmentSettingsBinding
import com.example.wellnesstracker.data.PrefsRepository
import com.example.wellnesstracker.util.NotificationUtil
import com.example.wellnesstracker.util.ReminderReceiver

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: PrefsRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        repo = PrefsRepository(requireContext())
        NotificationUtil.createNotificationChannel(requireContext())

        binding.switchHydration.isChecked = repo.isHydrationEnabled()
        binding.inputInterval.setText(repo.getHydrationIntervalMinutes().toString())

        binding.switchHydration.setOnCheckedChangeListener { _, checked ->
            repo.setHydrationEnabled(checked)
            updateAlarms()
        }
        binding.btnSaveInterval.setOnClickListener {
            val minutes = binding.inputInterval.text.toString().toIntOrNull() ?: 120
            repo.setHydrationIntervalMinutes(minutes)
            updateAlarms()
        }

        return binding.root
    }

    private fun updateAlarms() {
        val enabled = repo.isHydrationEnabled()
        val minutes = repo.getHydrationIntervalMinutes()
        val alarmManager = requireContext().getSystemService<AlarmManager>()
        val pending = PendingIntent.getBroadcast(
            requireContext(), 1001,
            Intent(requireContext(), ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pending)
        if (enabled) {
            val trigger = System.currentTimeMillis() + minutes * 60_000L
            alarmManager?.setRepeating(
                AlarmManager.RTC_WAKEUP, trigger, minutes * 60_000L, pending
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


