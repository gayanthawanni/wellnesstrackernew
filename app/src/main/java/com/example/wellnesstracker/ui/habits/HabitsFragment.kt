package com.example.wellnesstracker.ui.habits

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnesstracker.R
import com.example.wellnesstracker.data.PrefsRepository
import com.example.wellnesstracker.databinding.FragmentHabitsBinding
import com.example.wellnesstracker.model.Habit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitsFragment : Fragment() {
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: PrefsRepository
    private lateinit var adapter: HabitsListAdapter

    private val todayKey: String by lazy {
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        repository = PrefsRepository(requireContext())

        setupRecycler()
        setupClickListeners()
        updateProgressDisplay()

        return binding.root
    }

    private fun setupRecycler() {
        val habits = repository.getHabits()
        adapter = HabitsListAdapter(
            onToggleComplete = ::toggleComplete,
            onEdit = ::editHabit,
            onDelete = ::deleteHabit
        )
        adapter.update(habits)
        binding.recyclerHabits.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHabits.adapter = adapter
        
        // Setup swipe-to-complete functionality
        setupSwipeToComplete()
    }
    
    private fun setupSwipeToComplete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val habit = adapter.getItemAt(position)
                toggleComplete(habit)
                // Refresh the view after swiping
                adapter.notifyItemChanged(position)
            }
        }
        
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerHabits)
    }
    
    private fun updateProgressDisplay() {
        val habits = repository.getHabits()
        if (habits.isEmpty()) {
            binding.widgetProgress.text = "0%"
            binding.progressBar.progress = 0
            return
        }
        
        val completedCount = habits.count { it.isCompletedToday(todayKey) }
        val totalCount = habits.size
        val progressPercentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
        
        binding.widgetProgress.text = "$progressPercentage%"
        binding.progressBar.progress = progressPercentage
    }

    private fun setupClickListeners() {
        binding.fabAddHabit.setOnClickListener { showAddDialog() }
    }

    private fun showAddDialog() {
        val input = EditText(requireContext()).apply {
            hint = getString(R.string.habit_name_hint)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_habit)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
                val title = input.text.toString().trim()
                if (title.isNotEmpty()) {
                    val color = ContextCompat.getColor(requireContext(), R.color.habit_blue)
                    val list = repository.getHabits() ?: mutableListOf()
                    
                    // Create new habit
                    val newHabit = Habit(
                        id = System.currentTimeMillis(),
                        title = title,
                        color = color,
                        completionHistory = mutableListOf(),
                        streak = 0,
                        targetDaysPerWeek = 7
                    )
                    
                    // Add to list and save
                    list.add(newHabit)
                    repository.saveHabits(list)
                    adapter.update(list)
                    updateProgressDisplay()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun editHabit(habit: Habit) {
        val input = EditText(requireContext()).apply {
            setText(habit.title)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_habit)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
                val newTitle = input.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    // Find the habit in the list and update it
                    val list = repository.getHabits()
                    val habitIndex = list.indexOfFirst { it.id == habit.id }
                    if (habitIndex != -1) {
                        list[habitIndex].title = newTitle
                        repository.saveHabits(list)
                        adapter.update(list)
                        updateProgressDisplay()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteHabit(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_habit))
            .setMessage(getString(R.string.delete_habit_confirm, habit.title))
            .setPositiveButton(R.string.delete) { _, _ ->
                val list = repository.getHabits()
                list.removeAll { it.id == habit.id }
                repository.saveHabits(list)
                adapter.update(list)
                updateProgressDisplay()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun toggleComplete(habit: Habit) {
        val list = repository.getHabits()
        val idx = list.indexOfFirst { it.id == habit.id }
        if (idx != -1) {
            val current = list[idx]
            val isCompletedToday = current.completionHistory.contains(todayKey)

            if (isCompletedToday) {
                // unmark completion
                current.completionHistory.remove(todayKey)
                if (current.streak > 0) current.streak -= 1
                // clear lastCompletedDate if it was today
                if (current.lastCompletedDate == todayKey) {
                    current.lastCompletedDate = null
                }
            } else {
                // mark as complete
                current.completionHistory.add(todayKey)
                current.lastCompletedDate = todayKey
                current.streak += 1
            }

            list[idx] = current
            repository.saveHabits(list)
            adapter.update(list)
            
            // Update progress display after toggling completion
            updateProgressDisplay()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}