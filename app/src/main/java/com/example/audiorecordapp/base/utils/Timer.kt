package com.example.audiorecordapp.base.utils

import java.util.*
import java.util.Timer
class Timer(private var listener: OnTimerUpdateListener) {

    interface OnTimerUpdateListener {
        fun onTimerUpdate(duration: String)
    }

    private var duration: Long = 0
    private var period: Long = 258
    private lateinit var timer: Timer

    fun start() {
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                duration += period
                listener.onTimerUpdate(format())
            }
        }, period, period)
    }

    fun pause() {
        timer.cancel()
    }


    fun stop() {
        timer.cancel()
        timer.purge()
    }

    fun getDuration(): Long {
        return duration
    }


    fun format(): String {
        val milli = duration % 1000
        val seconds = (duration / 1000) % 60
        return "%02d:%02d".format(seconds, milli / 10)
    }
}