package com.example.byciclecomputer

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : ComponentActivity() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private val deviceAddress = "98:DA:50:02:C1:18"

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestBluetoothPermission()

        val db = AppDatabase.getDatabase(this)

        userDao = db.userDao()

        setContent {
            BluetoothScreen()
        }
    }


    @SuppressLint("MissingPermission")
    @Composable
    fun BluetoothScreen() {
        var isDone by remember { mutableStateOf(true) }
        var hasUpdate by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }
        var isDataLoaded by remember { mutableStateOf(false) }
        var isConnected by remember { mutableStateOf(false) }
        var showConnectionError by remember { mutableStateOf(false) }
        var tireSize by remember { mutableStateOf("") }
        var bodyWeight by remember { mutableStateOf("") }
        var time by remember { mutableIntStateOf(0) }
        var name by remember { mutableStateOf("") }
        val week by remember { mutableStateOf(Week(
            monday = Day(0.0, 0.0, 0.0,0),
            tuesday = Day(0.0, 0.0, 0.0,0),
            wednesday = Day(0.0, 0.0, 0.0,0),
            thursday = Day(0.0, 0.0, 0.0,0),
            friday = Day(0.0, 0.0, 0.0,0),
            saturday = Day(0.0, 0.0, 0.0,0),
            sunday = Day(0.0, 0.0, 0.0,0)
        )) }
        var distance by remember { mutableStateOf(0.0) }
        var calories by remember { mutableStateOf(0.0) }
        var k by remember { mutableStateOf(0f) }
        var rpm by remember { mutableStateOf(0f) }
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            loadUserData { userData ->
                userData?.let {
                    tireSize = it.tireSize.toString()
                    bodyWeight = it.bodyWeight.toString()
                    name = it.name
                    week.loadFromUserData(it)

                    resetDataAtStartOfWeek(context, week) {
                        saveUserData(bodyWeight.toDoubleOrNull() ?: 0.0, tireSize.toDoubleOrNull() ?: 0.0, name ?: "", week)
                    }
                }
                isDataLoaded = true
            }
        }


        val validTireSize = tireSize.toDoubleOrNull() ?: 0.0
        val validBodyWeight = bodyWeight.toDoubleOrNull() ?: 0.0

        LaunchedEffect(isDone) {
            while (!isDone) {
                delay(1000)
                time++
                val newData = readBluetoothData() ?: "Greška pri čitanju"
                parseBluetoothData(newData) { parsedRpm, parsedK ->
                    rpm = parsedRpm
                    k = parsedK
                }
                distance = calculateDistance(k, validTireSize)
                calories += roundToTwoDecimals(calculateCalories( rpm, validTireSize, validBodyWeight))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Happy ${getDayOfWeek()} $name, are you ready to ride your bicycle?",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF2ECC71),
                fontSize = 35.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
            if(isConnected) {
                InfoCard(
                    title = { Text("Velocity", color = Color(0xFF2ECC71)) },
                    value = calculateVelocity(rpm, validTireSize).toString()
                )
                InfoCard(title = { Text("Distance", color = Color(0xFF2ECC71)) }, value = "%.2f".format(distance))
                InfoCard(title = { Text("Calories", color = Color(0xFF2ECC71)) }, value = "%.2f".format(calories))
                InfoCard(title = { Text("Duration", color = Color(0xFF2ECC71)) }, value = formatTime(time))

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            isDone = !isDone
                            sendBluetoothData("B", if (isDone) "1" else "0")
                            if(isDone){
                                showDialog = true
                                updateWeek(distance, calories, time, week)
                                hasUpdate = true
                            }
                            saveUserData(
                                bodyWeight.toDoubleOrNull() ?: 0.0,
                                tireSize.toDoubleOrNull() ?: 0.0,
                                name ?: "",
                                week
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                        modifier = Modifier
                            .width(250.dp)
                            .padding(horizontal = 40.dp)
                    ) {
                        Text(if (isDone) "Start" else "Stop", color = Color.Black)
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    },
                    confirmButton = {
                        Button(onClick = {
                            showDialog = false
                            distance = 0.0
                            calories = 0.0
                            time = 0
                            rpm = 0f
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                            ) {
                            Text("OK", color = Color.Black)
                        }
                    },
                    title = { Text("Training Summary", color = Color(0xFF2ECC71)) },
                    text = {
                        Column {
                            Text("Average velocity: ${"%.2f".format(distance / time * 3600)} km/h", color = Color(0xFF2ECC71))
                            Text("Distance: ${"%.2f".format(distance)} km", color = Color(0xFF2ECC71))
                            Text("Calories: ${"%.2f".format(calories)} kcal", color = Color(0xFF2ECC71))
                            Text("Duration: ${formatTime(time)}", color = Color(0xFF2ECC71))
                        }
                    },
                    containerColor = Color.Black
                )
            }

            if(!isConnected) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val coroutineScope = rememberCoroutineScope()

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isConnected = connectToBluetooth(bodyWeight, tireSize, {
                                    showConnectionError = true
                                })
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                        modifier = Modifier
                            .width(250.dp)
                            .padding(horizontal = 40.dp)
                    ) {
                        Text("Connect to the device", color = Color.Black)
                    }
                }
                if (showConnectionError) {
                    CannotConnect { showConnectionError = false }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if(!hasUpdate){
                if (isDataLoaded) {
                    GraphScreen(week)
                } else {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }}
            else {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                loadUserData { userData ->
                    userData?.let {
                        week.loadFromUserData(it)
                        }
                    }
                hasUpdate = false;
                }


            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = tireSize,
                onValueChange = {
                    tireSize = it
                    saveUserData(bodyWeight.toDoubleOrNull() ?: 0.0, it.toDoubleOrNull() ?: 0.0, name ?: "", week)

                    if (isConnected) {
                        sendBluetoothData("T", tireSize)
                    }
                },
                label = { Text("Enter tire size(m)", color = Color.White) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = bodyWeight,
                onValueChange = {
                    bodyWeight = it
                    saveUserData(it.toDoubleOrNull() ?: 0.0, tireSize.toDoubleOrNull() ?: 0.0, name ?: "", week)

                    if (isConnected) {
                        sendBluetoothData("M", bodyWeight)
                    }
                },
                label = { Text("Enter weight(kg)", color = Color.White) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
            )


            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    saveUserData(bodyWeight.toDoubleOrNull() ?: 0.0, tireSize.toDoubleOrNull() ?: 0.0, it, week)
                },
                label = { Text("Enter your name", color = Color.White) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White)
            )
        }
    }


    fun Week.loadFromUserData(userData: UserData) {
        GetMonday().setAtributes(userData.mondayCalories, userData.mondayDistance, userData.mondayAverageVelocity,userData.mondayTime)
        GetTuesday().setAtributes(userData.tuesdayCalories, userData.tuesdayDistance, userData.tuesdayAverageVelocity, userData.tuesdayTime)
        GetWednesday().setAtributes(userData.wednesdayCalories, userData.wednesdayDistance, userData.wednesdayAverageVelocity,userData.wednesdayTime)
        GetThursday().setAtributes(userData.thursdayCalories, userData.thursdayDistance, userData.thursdayAverageVelocity,userData.thursdayTime)
        GetFriday().setAtributes(userData.fridayCalories, userData.fridayDistance, userData.fridayAverageVelocity,userData.fridayTime)
        GetSaturday().setAtributes(userData.saturdayCalories, userData.saturdayDistance, userData.saturdayAverageVelocity,userData.saturdayTime)
        GetSunday().setAtributes(userData.sundayCalories, userData.sundayDistance, userData.sundayAverageVelocity,userData.sundayTime)
    }


    fun parseBluetoothData(data: String, onParsed: (Float, Float) -> Unit) {
        val parsedValues = data.trim().split(" ").filter { it.isNotEmpty() }
        var rpm = 0f
        var k = 0f
        for (value in parsedValues) {
            when {
                value.startsWith("R:") -> rpm = value.substring(2).toFloatOrNull() ?: 0f
                value.startsWith("K:") -> k = value.substring(2).toFloatOrNull() ?: 0f
            }
        }
        onParsed(rpm, k)
    }


    private fun requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ), 1
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToBluetooth(
        bodyWeight: String,
        tireSize: String,
        onConnectionFailed: () -> Unit
    ): Boolean {
        val device: BluetoothDevice? = bluetoothAdapter?.bondedDevices?.find { it.address == deviceAddress }
        device?.let {
            val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            try {
                bluetoothSocket = it.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()
                sendBluetoothData("M", bodyWeight)
                sendBluetoothData("T", tireSize)
                return true
            } catch (e: IOException) {
                onConnectionFailed()
                return false
            }
        }
        onConnectionFailed()
        return false
    }




    private fun readBluetoothData(): String? {
        return try {
            val inputStream: InputStream? = bluetoothSocket?.inputStream
            val buffer = ByteArray(256)
            val bytesRead = inputStream?.read(buffer) ?: -1



            if (bytesRead > 0) {
                val receivedText = String(buffer, 0, bytesRead).trim()
                Log.d("Bluetooth", "Primljeni podaci: $receivedText")
                return receivedText
            } else {
                Log.d("Bluetooth", "Nema novih podataka")
                return null
            }
        } catch (e: IOException) {
            Log.e("Bluetooth", "Greška pri čitanju", e)
            return null
        }
    }


    private fun sendBluetoothData(type: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputStream: OutputStream? = bluetoothSocket?.outputStream
                val message = "$type:$value\n"
                outputStream?.write(message.toByteArray())
                outputStream?.flush()
                Log.d("Bluetooth", "Poslano: $message")
            } catch (e: IOException) {
                Log.e("Bluetooth", "Greška pri slanju", e)
            }
        }
    }

    private fun calculateExtent(tireSize: Double) : Double{
        return tireSize * 3.14
    }
    private fun calculateDistance(k : Float, tireSize : Double) : Double{
        if(k > 0) return roundToTwoDecimals(k * calculateExtent(tireSize) / 1000)
        return 0.0
    }

    private fun calculateVelocity( rpm: Float, tireSize: Double): Double {
        if (rpm > 0) return roundToTwoDecimals((calculateExtent(tireSize) * rpm) / 3.6)
        return 0.0
    }

    private fun calculateCalories( rpm : Float, tireSize : Double,bodyWeight : Double) : Double{
        var velocity = calculateVelocity(rpm,tireSize)
        var MET = 0.0
        if(velocity <= 16) MET = 4.0
        else if(velocity <= 19) MET = 6.8
        else if(velocity <= 22) MET = 8.0
        else if(velocity <= 26) MET = 10.0
        else MET = 12.0
        if(velocity > 0) return roundToTwoDecimals((bodyWeight * MET * 1 / 3600.0))
        return 0.0
    }

    private fun calculateNewAverageVelocity(
        distance: Double,
        newTime: Int,
        currentTime: Int
    ): Double {
        val totalTime = currentTime + newTime

        return if (totalTime > 0) distance / (totalTime.toDouble() / 3600.0) else 0.0
    }


    private fun updateWeek(distance: Double, calories: Double, time: Int, week: Week) {
        val day = getDayOfWeek()

        val dayData = when (day) {
            "monday" -> week.GetMonday()
            "tuesday" -> week.GetTuesday()
            "wednesday" -> week.GetWednesday()
            "thursday" -> week.GetThursday()
            "friday" -> week.GetFriday()
            "saturday" -> week.GetSaturday()
            "sunday" -> week.GetSunday()
            else -> return
        }

        val newDistance = dayData.getDistance() + distance
        val newCalories = dayData.getCalories() + calories
        val newAverageVelocity = calculateNewAverageVelocity(newDistance, dayData.getTime(), time)

        dayData.setAtributes(newCalories, newDistance, newAverageVelocity, time)
    }



    private fun getDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "monday"
            Calendar.TUESDAY -> "tuesday"
            Calendar.WEDNESDAY -> "wednesday"
            Calendar.THURSDAY -> "thursday"
            Calendar.FRIDAY -> "friday"
            Calendar.SATURDAY -> "saturday"
            Calendar.SUNDAY -> "sunday"
            else -> "Nepoznat dan"
        }
    }



    private fun saveUserData(bodyWeight: Double, tireSize: Double, name: String, week: Week) {
        CoroutineScope(Dispatchers.IO).launch {
            val userData = UserData(
                bodyWeight = bodyWeight,
                tireSize = tireSize,
                name = name,
                mondayCalories = week.GetMonday().getCalories(),
                mondayDistance = week.GetMonday().getDistance(),
                mondayAverageVelocity = week.GetMonday().getAverageVelocity(),
                tuesdayCalories = week.GetTuesday().getCalories(),
                tuesdayDistance = week.GetTuesday().getDistance(),
                tuesdayAverageVelocity = week.GetTuesday().getAverageVelocity(),
                wednesdayCalories = week.GetWednesday().getCalories(),
                wednesdayDistance = week.GetWednesday().getDistance(),
                wednesdayAverageVelocity = week.GetWednesday().getAverageVelocity(),
                thursdayCalories = week.GetThursday().getCalories(),
                thursdayDistance = week.GetThursday().getDistance(),
                thursdayAverageVelocity = week.GetThursday().getAverageVelocity(),
                fridayCalories = week.GetFriday().getCalories(),
                fridayDistance = week.GetFriday().getDistance(),
                fridayAverageVelocity = week.GetFriday().getAverageVelocity(),
                saturdayCalories = week.GetSaturday().getCalories(),
                saturdayDistance = week.GetSaturday().getDistance(),
                saturdayAverageVelocity = week.GetSaturday().getAverageVelocity(),
                sundayCalories = week.GetSunday().getCalories(),
                sundayDistance = week.GetSunday().getDistance(),
                sundayAverageVelocity = week.GetSunday().getAverageVelocity(),
                mondayTime = week.GetMonday().getTime(),
                tuesdayTime = week.GetTuesday().getTime(),
                wednesdayTime = week.GetWednesday().getTime(),
                thursdayTime = week.GetThursday().getTime(),
                fridayTime = week.GetFriday().getTime(),
                saturdayTime = week.GetSaturday().getTime(),
                sundayTime = week.GetSunday().getTime()
            )
            userDao.insert(userData)
        }
    }

    @Composable
    private fun CannotConnect(onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)),
                ) {
                    Text("OK", color = Color.Black)
                }
            },
            title = { Text("Connectivity Issues", color = Color(0xFF2ECC71)) },
            text = { Text("Please make sure the device is turned on, and you are close enough to it.", color = Color(0xFF2ECC71)) },
            containerColor = Color.Black
        )
    }


    private fun loadUserData(onDataLoaded: (UserData?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val userData = userDao.getUserData()
            withContext(Dispatchers.Main) {
                onDataLoaded(userData)
            }
        }
    }

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

}


fun roundToTwoDecimals(value: Double): Double {
    return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
}

@Composable
fun InfoCard(
    title: @Composable () -> Unit,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            title()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}









