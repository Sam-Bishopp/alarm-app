package com.example.alarmapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class ActiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active)

        //val db = AlarmsDatabase.getDatabase(application)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(1)?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var menuSelected = ""

        when(item.itemId) {
            R.id.menuAlarm -> menuSelected = "Alarm"
            R.id.menuTimer -> menuSelected = "Timer"
            //R.id.menuActive -> menuSelected = "Active Alarms"
        }
        Toast.makeText(this, "Option : $menuSelected", Toast.LENGTH_SHORT).show()

        if (id == R.id.menuAlarm) {
            val alarmIntent = Intent(this, MainActivity::class.java)
            this.startActivity(alarmIntent)
            return true
        }

        if (id == R.id.menuTimer) {
            val timerIntent = Intent(this, TimerActivity::class.java)
            this.startActivity(timerIntent)
            return true
        }

        /*if (id == R.id.menuActive) {
            val activeAlarmsIntent = Intent(this, ActiveActivity::class.java)
            this.startActivity(activeAlarmsIntent)
            return true
        }*/
        return super.onOptionsItemSelected(item)
    }
}