package com.example.alarmapp

// import android.content.Context
// import androidx.room.Database
// import androidx.room.Room
// import androidx.room.RoomDatabase
//
// @Database(entities = [Alarms::class], version = 1, exportSchema = false)
// public abstract class AlarmsDatabase: RoomDatabase() {
// abstract fun alarmsDao(): AlarmsDao
//
// companion object {
// var instance: AlarmsDatabase? = null
//
// fun getDatabase(context: Context): AlarmsDatabase {
// var tmpInstance = instance
//
// if(tmpInstance == null) {
// tmpInstance = Room.databaseBuilder(
// context.applicationContext,
// AlarmsDatabase::class.java,
// "alarmsDatabase"
// ).build()
// instance = tmpInstance
// }
// return tmpInstance
// }
// }
// }