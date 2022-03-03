package com.pribha.womenssafetyandsecurityapp.helper.shakeDetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ShakeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (null != intent && intent.action.equals("shake.detector")) {

            Log.d("event", "onShake")
        }


        if (intent?.action.equals(Intent.ACTION_SCREEN_OFF)) {
            //DO HERE
            Log.d("event101", "screenOFF")

        } else if (intent?.action.equals(Intent.ACTION_SCREEN_ON)) {
            //DO HERE
            Log.d("event101", "screenON")

        }

    }
}