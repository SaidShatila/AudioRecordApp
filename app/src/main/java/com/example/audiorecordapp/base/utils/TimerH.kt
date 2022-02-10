package com.example.audiorecordapp.base.utils

import android.os.Handler
import android.os.Looper

const val delay = 100L

class TimerH(private val listener: OnTimerUpdateListener) {

    interface OnTimerUpdateListener {
        fun onTimerTicks(duration: String)
    }

    private var handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var duration = 0L

    init {
        runnable = Runnable {
            duration += delay
            handler.postDelayed(runnable, delay)

            listener.onTimerTicks(format())
        }
    }

    fun start() {
        handler.postDelayed(runnable, delay)
    }

    fun pause() {
        handler.removeCallbacks(runnable)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        listener.onTimerTicks("00.00")
        duration = 0L
    }

    private fun format(): String {
        val milli = duration % 1000
        val seconds = (duration / 1000) % 60


        return "%02d:%02d".format(seconds, milli / 10)

    }
}