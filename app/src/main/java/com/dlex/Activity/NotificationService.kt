package com.dlex.Activity

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.dlex.Helper.AlertNotification
import java.util.*

class NotificationService: Service() {

    private val timer = Timer()
    private val alertNotification = AlertNotification()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startService()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startService() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                runTask()
            }
        }, 60000)
    }

    private fun runTask() {
        alertNotification.alertUpdate(this)
    }
}
