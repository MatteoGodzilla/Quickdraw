package com.example.quickdraw

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.quickdraw.ui.theme.QuickdrawTheme

class SensorTest : ComponentActivity(), SensorEventListener {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        sensorManager.registerListener(this,
            gravitySensor,
            SensorManager.SENSOR_DELAY_GAME,
         SensorManager.SENSOR_DELAY_GAME
        )

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        enableEdgeToEdge()
        setContent {
            QuickdrawTheme {

            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event != null){
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            Log.i(TAG, "SENSOR: $x $y $z")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.i(TAG, "ACCURACY: $sensor $accuracy")
    }
}