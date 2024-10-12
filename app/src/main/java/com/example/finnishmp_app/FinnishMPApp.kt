package com.example.finnishmp_app

import android.app.Application
import android.content.Context
import com.example.finnishmp_app.db.DatabaseSyncManager
/*
Muche Berhanu 2219580
FinnishMPApp is a subclass for the Finnish MP application initializes the application context
and starts the DatabaseSyncManager during the application's creation process.   */
class FinnishMPApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeAppContext()
        startDatabaseSync()
    }

    private fun initializeAppContext() {
        appContext = applicationContext
    }

    private fun startDatabaseSync() {
        DatabaseSyncManager.start()
    }

    companion object {
        lateinit var appContext: Context
            private set // Restrict external modification of appContext
    }
}