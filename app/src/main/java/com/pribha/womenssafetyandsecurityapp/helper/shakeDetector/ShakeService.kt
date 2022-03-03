package com.pribha.womenssafetyandsecurityapp.helper.shakeDetector

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.Service.START_NOT_STICKY
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.pribha.womenssafetyandsecurityapp.MainActivity
import com.pribha.womenssafetyandsecurityapp.R


class ShakeService : Service() {
    private var appPreferences: AppPreferences? = null
    private var shakeOptions: ShakeOptions? = null
    private var shakeListener: ShakeListener? = null
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    private val CHANNEL_ID = "ForegroundService Kotlin"

    override fun onCreate() {
        appPreferences = AppPreferences(getBaseContext())
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        shakeOptions = ShakeOptions()
            .background(appPreferences!!.getBoolean("BACKGROUND", true))
            .sensibility(appPreferences!!.getFloat("SENSIBILITY", 1.2f))
            .shakeCount(appPreferences!!.getInt("SHAKE_COUNT", 1))
            .interval(appPreferences!!.getInt("SHAKE_INTERVAL", 2000))
        startShakeService(getBaseContext())


        //do heavy work on a background thread
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Background service On")
            .setContentText("Tracking Live Location")
            .setSmallIcon(R.drawable.ic_action_search)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)



        return if (shakeOptions!!.isBackground) {
            START_STICKY
        } else {
            START_NOT_STICKY
        }
    }

    fun startShakeService(context: Context) {
        shakeListener = ShakeListener(shakeOptions, context)
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        val sensors: List<Sensor> = sensorManager!!.getSensorList(Sensor.TYPE_ACCELEROMETER)
        if (sensors.size > 0) {
            sensor = sensors[0]
            sensorManager!!.registerListener(shakeListener, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onDestroy() {
        sensorManager!!.unregisterListener(shakeListener)
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }


    companion object {
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ShakeService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, ShakeService::class.java)
            context.stopService(stopIntent)
        }
    }

}
