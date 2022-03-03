package com.pribha.womenssafetyandsecurityapp.helper.shakeDetector

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager


class ShakeDetector {
    private var sensorManager: SensorManager? = null
    private val context: Context? = null
    private var shakeCallback: ShakeCallback? = null
    private var sensor: Sensor? = null
    var isRunning = false
        private set
    private var shakeOptions: ShakeOptions? = null
    private var appPreferences: AppPreferences? = null
    private var shakeBroadCastReceiver: ShakeBroadCastReceiver? = null
    private var shakeListener: ShakeListener? = null

    constructor() {}
    constructor(shakeOptions: ShakeOptions?) {
        this.shakeOptions = shakeOptions
    }

    fun start(context: Context, shakeCallback: ShakeCallback?): ShakeDetector {
        this.shakeCallback = shakeCallback
        shakeBroadCastReceiver = ShakeBroadCastReceiver(shakeCallback)
        registerPrivateBroadCast(context)
        saveOptionsInStorage(context)
        startShakeService(context)
        return this
    }

    fun start(context: Context): ShakeDetector {
        saveOptionsInStorage(context)
        startShakeService(context)
        return this
    }

    fun destroy(context: Context) {
        if (shakeBroadCastReceiver != null) {
            context.unregisterReceiver(shakeBroadCastReceiver)
        }
    }

    fun stopShakeDetector(context: Context) {
        context.stopService(Intent(context, ShakeService::class.java))
    }

    private fun startShakeService(context: Context) {
        val serviceIntent = Intent(context, ShakeService::class.java)
        context.startService(serviceIntent)
    }

    fun startService(context: Context): ShakeDetector {
        shakeListener = ShakeListener(shakeOptions, context)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors = sensorManager!!.getSensorList(Sensor.TYPE_ACCELEROMETER)
        if (sensors.size > 0) {
            sensor = sensors[0]
            isRunning = sensorManager!!.registerListener(shakeListener, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
        return this
    }

    fun saveOptionsInStorage(context: Context?) {
        appPreferences = AppPreferences(context)
        appPreferences!!.putBoolean("BACKGROUND", shakeOptions!!.isBackground)
        appPreferences!!.putInt("SHAKE_COUNT", shakeOptions!!.shakeCounts)
        appPreferences!!.putInt("SHAKE_INTERVAL", shakeOptions!!.interval)
        appPreferences!!.putFloat("SENSIBILITY", shakeOptions!!.sensibility)
    }

    private fun registerPrivateBroadCast(context: Context) {
        val filter = IntentFilter()
        filter.addAction("shake.detector")
        filter.addAction("private.shake.detector")
        context.registerReceiver(shakeBroadCastReceiver, filter)
    }
}


