package com.theapache64.swipenetic.utils

import android.os.Handler

class Repeater(private val interval: Long) : Runnable {

    private lateinit var task: () -> Unit
    private val handler = Handler()

    fun cancel() {
        handler.removeCallbacks(this)
    }

    fun startExecute(task: () -> Unit) {
        this.task = task
        handler.postDelayed(this, interval)
    }

    override fun run() {
        this.task()
        startExecute(task)
    }

}