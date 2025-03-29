package com.example.byciclecomputer

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter


@Composable
fun DistanceChart(week: Week) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setTouchEnabled(true)
                setPinchZoom(true)

                val entries = listOf(
                    BarEntry(0f, week.GetMonday().getDistance().toFloat()),
                    BarEntry(1f, week.GetTuesday().getDistance().toFloat()),
                    BarEntry(2f, week.GetWednesday().getDistance().toFloat()),
                    BarEntry(3f, week.GetThursday().getDistance().toFloat()),
                    BarEntry(4f, week.GetFriday().getDistance().toFloat()),
                    BarEntry(5f, week.GetSaturday().getDistance().toFloat()),
                    BarEntry(6f, week.GetSunday().getDistance().toFloat())
                )

                val dataSet = BarDataSet(entries, "Distance").apply {
                    color = Color.parseColor("#2ECC71")
                    valueTextSize = 12f
                    valueTextColor = Color.WHITE
                }

                val barData = BarData(dataSet)
                barData.barWidth = 0.3f

                this.data = barData

                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return days.getOrNull(value.toInt()) ?: value.toString()
                    }
                }

                xAxis.textColor = Color.WHITE
                legend.textColor = Color.WHITE


                invalidate()
            }
        }
    )
}

@Composable
fun VelocityChart(week: Week) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setTouchEnabled(true)
                setPinchZoom(true)

                val entries = listOf(
                    BarEntry(0f, week.GetMonday().getAverageVelocity().toFloat()),
                    BarEntry(1f, week.GetTuesday().getAverageVelocity().toFloat()),
                    BarEntry(2f, week.GetWednesday().getAverageVelocity().toFloat()),
                    BarEntry(3f, week.GetThursday().getAverageVelocity().toFloat()),
                    BarEntry(4f, week.GetFriday().getAverageVelocity().toFloat()),
                    BarEntry(5f, week.GetSaturday().getAverageVelocity().toFloat()),
                    BarEntry(6f, week.GetSunday().getAverageVelocity().toFloat())
                )

                val dataSet = BarDataSet(entries, "Velocity").apply {
                    color = Color.parseColor("#E74C3C")
                    valueTextSize = 12f
                    valueTextColor = Color.WHITE
                }

                val barData = BarData(dataSet)
                barData.barWidth = 0.3f

                this.data = barData

                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return days.getOrNull(value.toInt()) ?: value.toString()
                    }
                }

                xAxis.textColor = Color.WHITE
                legend.textColor = Color.WHITE



                invalidate()
            }
        }
    )
}

@Composable
fun CaloriesChart(week: Week) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setTouchEnabled(true)
                setPinchZoom(true)

                val entries = listOf(
                    BarEntry(0f, week.GetMonday().getCalories().toFloat()),
                    BarEntry(1f, week.GetTuesday().getCalories().toFloat()),
                    BarEntry(2f, week.GetWednesday().getCalories().toFloat()),
                    BarEntry(3f, week.GetThursday().getCalories().toFloat()),
                    BarEntry(4f, week.GetFriday().getCalories().toFloat()),
                    BarEntry(5f, week.GetSaturday().getCalories().toFloat()),
                    BarEntry(6f, week.GetSunday().getCalories().toFloat())
                )

                val dataSet = BarDataSet(entries, "Calories").apply {
                    color = Color.parseColor("#4A90E2")
                    valueTextSize = 9f
                    valueTextColor = Color.WHITE
                }

                val barData = BarData(dataSet)
                barData.barWidth = 0.3f

                this.data = barData

                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return days.getOrNull(value.toInt()) ?: value.toString()
                    }
                }

                xAxis.textColor = Color.WHITE
                legend.textColor = Color.WHITE

                invalidate()
            }
        }
    )
}

    @Composable
    fun TimeChart(week: Week) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = { context ->
                BarChart(context).apply {
                    description.isEnabled = false
                    setDrawGridBackground(false)
                    setDrawBarShadow(false)
                    setTouchEnabled(true)
                    setPinchZoom(true)

                    val entries = listOf(
                        BarEntry(0f, week.GetMonday().getTime().toFloat() / 3600) ,
                        BarEntry(1f, week.GetTuesday().getTime().toFloat()/ 3600),
                        BarEntry(2f, week.GetWednesday().getTime().toFloat()/ 3600),
                        BarEntry(3f, week.GetThursday().getTime().toFloat()/ 3600),
                        BarEntry(4f, week.GetFriday().getTime().toFloat()/ 3600),
                        BarEntry(5f, week.GetSaturday().getTime().toFloat()/ 3600),
                        BarEntry(6f, week.GetSunday().getTime().toFloat()/ 3600)
                    )

                    val dataSet = BarDataSet(entries, "Time").apply {
                        color = Color.parseColor("#F39C12")
                        valueTextSize = 9f
                        valueTextColor = Color.WHITE
                    }

                    val barData = BarData(dataSet)
                    barData.barWidth = 0.3f

                    this.data = barData

                    val days = listOf("M", "T", "W", "T", "F", "S", "S")
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return days.getOrNull(value.toInt()) ?: value.toString()
                        }
                    }

                    xAxis.textColor = Color.WHITE
                    legend.textColor = Color.WHITE

                    invalidate()
                }
            }
        )
    }







