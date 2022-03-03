package com.pribha.womenssafetyandsecurityapp.helper

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.pribha.womenssafetyandsecurityapp.MainActivity
import java.io.FileOutputStream
import java.io.FileWriter


class BackgroundAccelerometerService : Service(), SensorEventListener {
    private var mInitialized = false
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var filepath: String? = null
    private var writer: FileWriter? = null
    private var output: FileOutputStream? = null

    // epoch time since last file write
    private var lastTime: Long = 0

    // minimum time in seconds to write to file after previous write
    private val period = 5
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prefs: SharedPreferences = this.getSharedPreferences(
            "com.nicolashahn.backgroundaccelerometer", Context.MODE_PRIVATE
        )
        filepath = prefs.getString("filepath", filepath)
        Log.e(
            LOG_TAG,
            "in BAService, filepath is $filepath"
        )
        try {
            output = FileOutputStream(filepath, true)
            writer = FileWriter(output!!.getFD())
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(
                LOG_TAG,
                "could not open file for writing, error $e"
            )
        }
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show()
        Log.d("Service Started", "Service Started")
        mInitialized = false
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show()
        Log.d("Service Destroyed", "Service Destroyed")
        try {
            writer?.close()
            output?.getFD()?.sync()
            output?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mSensorManager!!.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        if (!mInitialized) mInitialized = true
        val tsLong = System.currentTimeMillis() / 1000
        if (tsLong > lastTime + period) {
            lastTime = tsLong
            recordAccelData(x, y, z, tsLong)
        }
    }

    // write to file a line in format:
    // epochtime, x, y, z
    fun recordAccelData(x: Float, y: Float, z: Float, tsLong: Long) {
        val ts = tsLong.toString()
        val accelLine = """
             $ts, ${java.lang.Float.toString(x)}, ${java.lang.Float.toString(y)}, ${
            java.lang.Float.toString(z)
        }
             
             """.trimIndent()
        try {
            writer?.write(accelLine)
            writer?.flush()
            Log.e(LOG_TAG, "writing to file $accelLine")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(LOG_TAG, "exception when writing file in recordAccelData")
        }
    }

    companion object {
        val LOG_TAG: String = MainActivity::class.java.getSimpleName()
    }
}
