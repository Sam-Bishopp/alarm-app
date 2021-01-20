package com.example.alarmapp

/*import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class AlarmsViewModel(app: Application) : AndroidViewModel(app) {

    val db = AlarmsDatabase.getDatabase(app)
    var alarms: LiveData<List<Alarms>>

    init {
        alarms = db.alarmsDao().getAllAlarms()
    }

    fun insert(alarms: Alarms) {
        db.alarmsDao().insert(alarms)
    }

    fun getAllAlarms(): LiveData<List<Alarms>> {
        return alarms
    }
}*/