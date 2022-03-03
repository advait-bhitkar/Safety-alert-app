package com.pribha.womenssafetyandsecurityapp.helper.shakeDetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class ShakeBroadCastReceiver : BroadcastReceiver {
    private var callback: ShakeCallback? = null

    constructor() {}
    constructor(callback: ShakeCallback?) {
        this.callback = callback
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (null != intent && intent.action == "private.shake.detector") {
            callback!!.onShake()
        }
    }
}
