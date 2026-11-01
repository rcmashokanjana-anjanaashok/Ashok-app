package com.example

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.data.repository.DailyLifeRepository
import com.example.receiver.ReminderReceiver

class DailyLifeApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { DailyLifeRepository(database) }

    override fun onCreate() {
        super.onCreate()
        ReminderReceiver.createNotificationChannel(this)
    }
}
