package com.example.byciclecomputer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GraphScreen(week : Week){
    WeekStats(week)
}

@Composable
fun WeekStats(week: Week) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Distance", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        DistanceChart(week)

        Text("Calories Burned", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        CaloriesChart(week)

        Text("Average Speed", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        VelocityChart(week)

        Text("Time Spent", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        TimeChart(week)
    }
}