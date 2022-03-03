package com.pribha.womenssafetyandsecurityapp.helper.shakeDetector

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log


class ShakeListener : SensorEventListener {
    private var mShakeTimestamp: Long = 0
    private var mShakeCount = 0
    private var shakeOptions: ShakeOptions? = null
    private var context: Context? = null

    constructor() {}
    constructor(shakeOptions: ShakeOptions?) {
        this.shakeOptions = shakeOptions
    }

    constructor(shakeOptions: ShakeOptions?, context: Context?) {
        this.shakeOptions = shakeOptions
        this.context = context
    }

    constructor(shakeOptions: ShakeOptions?, context: Context?, callback: ShakeCallback?) {
        this.shakeOptions = shakeOptions
        this.context = context
    }

    interface OnShakeListener {
        fun onShake(count: Int)
    }

    fun resetShakeCount() {
        mShakeCount = 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // ignore
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH
        val gForce =
            Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()
        if (gForce > shakeOptions!!.sensibility) {
            Log.d("LISTENER", "force: $gForce count: $mShakeCount")
            val now = System.currentTimeMillis()
            if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return
            }
            if (mShakeTimestamp + shakeOptions!!.interval < now) {
                mShakeCount = 0
            }
            mShakeTimestamp = now
            mShakeCount++
            if (shakeOptions!!.shakeCounts == mShakeCount) {
                if (shakeOptions!!.isBackground) {
                    sendToBroadCasts(context)
                } else {
                    sendToPrivateBroadCasts(context)
                }
            }
        }
    }

    private fun sendToBroadCasts(context: Context?) {
        val locationIntent = Intent()
        locationIntent.action = "shake.detector"
        context?.sendBroadcast(locationIntent)
    }

    private fun sendToPrivateBroadCasts(context: Context?) {
        val locationIntent = Intent()
        locationIntent.action = "private.shake.detector"
        context?.sendBroadcast(locationIntent)
    }

    companion object {
        private const val SHAKE_SLOP_TIME_MS = 500
    }
}