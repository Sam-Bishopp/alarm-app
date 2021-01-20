package com.example.alarmapp

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var alarmManager : AlarmManager
    lateinit var timePicker: TimePickerDialog
    lateinit var datePicker : DatePickerDialog
    private lateinit var context: Context

    private val dateFormat = SimpleDateFormat("dd | MM | yyyy", Locale.UK)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.UK)
    var formatter: DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.UK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this
        val calendar = Calendar.getInstance()
        val curDom = calendar.get(Calendar.DAY_OF_MONTH)
        val curMonth = calendar.get(Calendar.MONTH)
        val curYear = calendar.get(Calendar.YEAR)
        val curHour = calendar.get(Calendar.HOUR_OF_DAY)
        val curMin = calendar.get(Calendar.MINUTE)

        btn_date.text = dateFormat.format(calendar.time)
        btn_time.text = timeFormat.format(calendar.time)

        var selectDom = curDom
        var selectMonth = curMonth
        var selectYear = curYear
        var selectHour = curHour
        var selectMin = curMin

        btn_date.setOnClickListener {
            datePicker = DatePickerDialog(
                this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    selectDom = dayOfMonth
                    selectMonth = month
                    selectYear = year
                    btn_date.text = dateFormat.format(selectedDate.time)
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        btn_time.setOnClickListener {
            timePicker = TimePickerDialog(
                this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)

                    selectHour = hourOfDay
                    selectMin = minute
                    btn_time.text = timeFormat.format(selectedTime.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePicker.show()
        }

        btn_set.setOnClickListener {
            val anAlarm = Calendar.Builder()
                .setDate(selectYear, selectMonth, selectDom)
                .setTimeOfDay(selectHour, selectMin, 0)
                .build()
            setAlarm(anAlarm)
        }
    }

    private fun setAlarm(anAlarm: Calendar) {
        //val db = AlarmsDatabase.getDatabase(application)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmId = System.currentTimeMillis().toInt()
        val millis: Long = anAlarm.timeInMillis

        val intent = Intent(context, MainReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val date = formatter.format(millis)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent)

        /*lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val newAlarm = Alarms(alarmId, date)
                db.alarmsDao().insert(newAlarm)
            }
        }*/
        Log.d("MainActivity", "Alarm set | Millis = $millis | Real time = $date")
        Toast.makeText(context, "Alarm set for: $date", Toast.LENGTH_LONG).show()
    }

    class MainReceiver : BroadcastReceiver() {

        private lateinit var nManager : NotificationManager
        private lateinit var nChannel : NotificationChannel
        private lateinit var nBuilder : Notification.Builder
        private val nChannelId = "Alarm Notification"
        private val nDescription = "Alarm Expired"
        private lateinit var mediaPlayer: MediaPlayer

        override fun onReceive(context: Context?, intent: Intent?) {
            nManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val playAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            mediaPlayer = MediaPlayer.create(context, playAlarmSound)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                nChannel = NotificationChannel(nChannelId, nDescription, NotificationManager.IMPORTANCE_HIGH)
                nChannel.enableLights(true)
                nChannel.lightColor = Color.RED
                nChannel.enableVibration(true)
                val resultsIntent = Intent(context, MainActivity::class.java)
                val resultPendingIntent = PendingIntent.getActivity(context, 1, resultsIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                nManager.createNotificationChannel(nChannel)
                nBuilder = Notification.Builder(context, nChannelId)
                    .setContentTitle("Alarm!")
                    .setContentText("An Alarm has Expired")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
            } else {
                val resultsIntent = Intent(context, MainActivity::class.java)
                val resultPendingIntent = PendingIntent.getActivity(context, 1, resultsIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                nBuilder = Notification.Builder(context)
                    .setContentTitle("Timer!")
                    .setContentText("Your Timer has Expired")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
            }

            fun playAudio() {
                val timer = object: CountDownTimer(20000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        mediaPlayer.start()
                        Log.d("MainAudio", "Playing Audio for 15 seconds...")
                    }

                    override fun onFinish() {
                        mediaPlayer.release()
                        Log.d("MainAudio", "Stopping Audio.")
                    }
                }
                timer.start()
            }
            nManager.notify(1, nBuilder.build())
            playAudio()
            Log.d("MainActivity", "Alarm Receiver: " + Date().toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(0)?.isVisible = false
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
        Toast.makeText(this, "Option : $menuSelected",Toast.LENGTH_SHORT).show()

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