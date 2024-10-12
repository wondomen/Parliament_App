package com.example.finnishmp_app.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finnishmp_app.FinnishMPApp

@Database(entities = [ParliamentMember::class], version = 1)
/*
Muche Berhanu 2219580
This class extends RoomDatabase, which is the main class for Room to manage the SQLite database.
*/
abstract class PMDatabase : RoomDatabase() {
    abstract fun memberDao(): PMDao

    companion object {
        @Volatile
        private var Instance: PMDatabase? = null

        fun getInstance(): PMDatabase {
            if (Instance == null) {
                synchronized(this) {
                    Instance = Room.databaseBuilder(
                       FinnishMPApp.appContext, PMDatabase::class.java, "eduskunta-db"
                    ).build() //Creates the database instance with a specified name ("eduskunta-db") using the application context.
                }
            }
            return Instance!!  //The non-null assertion operator is used because we know that the
        // instance will not be null after the check and synchronization block.
        }
    }
}