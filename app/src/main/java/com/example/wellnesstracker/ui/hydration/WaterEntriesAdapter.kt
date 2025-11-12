package com.example.wellnesstracker.ui.hydration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnesstracker.databinding.ItemWaterBinding
import com.example.wellnesstracker.model.WaterEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterEntriesAdapter(
    private var entries: MutableList<WaterEntry>
) : RecyclerView.Adapter<WaterEntriesAdapter.ViewHolder>() {

    // ✅ Only create formatter once — avoids overhead per bind
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    inner class ViewHolder(val binding: ItemWaterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWaterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]

        holder.binding.apply {
            textCupType.text = entry.cupType.displayName
            textAmount.text = "${entry.amountMl}ml"
            textTime.text = timeFormat.format(Date(entry.timestamp))
        }
    }

    override fun getItemCount(): Int = entries.size

    // ✅ More efficient updates using DiffUtil
    fun updateEntries(newEntries: MutableList<WaterEntry>) {
        val diffCallback = WaterEntriesDiffCallback(entries, newEntries)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        entries = newEntries
        diffResult.dispatchUpdatesTo(this)
    }

    // ✅ Helper DiffUtil Callback
    class WaterEntriesDiffCallback(
        private val oldList: List<WaterEntry>,
        private val newList: List<WaterEntry>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Typically compare unique IDs
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Compare full object equality
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}