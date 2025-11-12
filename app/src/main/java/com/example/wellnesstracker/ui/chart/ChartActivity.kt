package com.example.wellnesstracker.ui.chart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wellnesstracker.data.PrefsRepository
import com.example.wellnesstracker.databinding.ActivityChartBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = PrefsRepository(this)
        val moods = repo.getMoods()

        // Map days to counts in last 7 days
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val dayStart = cal.timeInMillis
        val oneDay = 24L * 60L * 60L * 1000L
        val entries = ArrayList<Entry>()
        for (i in 6 downTo 0) {
            val start = dayStart - i * oneDay
            val end = start + oneDay
            val count = moods.count { it.timestamp in start until end }
            entries.add(Entry((6 - i).toFloat(), count.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Moods per day").apply {
            setDrawCircles(true)
            lineWidth = 2f
        }
        binding.chart.data = LineData(dataSet)
        binding.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.chart.axisRight.isEnabled = false
        binding.chart.description.isEnabled = false
        binding.chart.invalidate()
    }
}


