package com.example.byciclecomputer

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.stream.DoubleStream.DoubleMapMultiConsumer

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey val id: Int = 1,
    val bodyWeight: Double,
    val tireSize: Double,
    val name: String,
    val mondayDistance: Double,
    val mondayCalories: Double,
    val mondayAverageVelocity: Double,
    val tuesdayDistance: Double,
    val tuesdayCalories: Double,
    val tuesdayAverageVelocity: Double,
    val wednesdayDistance: Double,
    val wednesdayCalories: Double,
    val wednesdayAverageVelocity: Double,
    val thursdayDistance: Double,
    val thursdayCalories: Double,
    val thursdayAverageVelocity: Double,
    val fridayDistance: Double,
    val fridayCalories: Double,
    val fridayAverageVelocity: Double,
    val saturdayDistance: Double,
    val saturdayCalories: Double,
    val saturdayAverageVelocity: Double,
    val sundayDistance: Double,
    val sundayCalories: Double,
    val sundayAverageVelocity: Double,
    val mondayTime : Int,
    val tuesdayTime : Int,
    val wednesdayTime : Int,
    val thursdayTime: Int,
    val fridayTime: Int,
    val saturdayTime: Int,
    val sundayTime: Int
    )

fun resetDataAtStartOfWeek(context: Context, week: Week, onReset: () -> Unit) {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val lastResetDate = sharedPreferences.getString("lastResetDate", "")

    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

    if (dayOfWeek == Calendar.MONDAY && lastResetDate != todayDate) {

        week.GetMonday().setAtributes(0.0, 0.0, 0.0,0)
        week.GetTuesday().setAtributes(0.0, 0.0, 0.0,0)
        week.GetWednesday().setAtributes(0.0, 0.0, 0.0,0)
        week.GetThursday().setAtributes(0.0, 0.0, 0.0,0)
        week.GetFriday().setAtributes(0.0, 0.0, 0.0,0)
        week.GetSaturday().setAtributes(0.0, 0.0, 0.0,0)
        week.GetSunday().setAtributes(0.0, 0.0, 0.0,0)

        sharedPreferences.edit().putString("lastResetDate", todayDate).apply()

        onReset()
    }
}





