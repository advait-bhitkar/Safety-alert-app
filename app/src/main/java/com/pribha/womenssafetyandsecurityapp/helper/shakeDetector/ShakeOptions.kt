package com.pribha.womenssafetyandsecurityapp.helper.shakeDetector

class ShakeOptions {
    var isBackground = true
    var shakeCounts = 0
    var interval = 0
        private set
    var sensibility = 0f
        private set

    fun background(background: Boolean): ShakeOptions {
        isBackground = background
        return this
    }

    fun shakeCount(shakeCount: Int): ShakeOptions {
        shakeCounts = shakeCount
        return this
    }

    fun interval(interval: Int): ShakeOptions {
        this.interval = interval
        return this
    }

    fun sensibility(sensibility: Float): ShakeOptions {
        this.sensibility = sensibility
        return this
    }
}
