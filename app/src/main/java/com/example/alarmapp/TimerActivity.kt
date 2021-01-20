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
import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*

class TimerActivity : AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        context = this
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        createTimer.setOnClickListener {
            val timerInt = enterTimer.text.toString().toInt()
            val toMillis = timerInt * 1000
            val intent = Intent(context, TimerReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            Log.d("TimerActivity", "Timer Created or Updated at: " + Date().toString() + " in $timerInt seconds")
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + toMillis, pendingIntent)
            Toast.makeText(context, "A timer has been set for $timerInt seconds", Toast.LENGTH_SHORT).show()
        }

        deleteTimer.setOnClickListener {
            val intent = Intent(context, TimerReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            Log.d("TimerActivity", "Cancel: " + Date().toString())
            Toast.makeText(context, "Your timer has been cancelled", Toast.LENGTH_SHORT).show()
            alarmManager.cancel(pendingIntent)
        }

        /*stopTimer.setOnClickListener {
                val intent = Intent(context, StopAudio::class.java)
                Log.d("TimerActivity", "Timer alarm audio stopped")
                Toast.makeText(context, "Timer alarm stopped", Toast.LENGTH_SHORT).show()
            }*/
    }

    class TimerReceiver : BroadcastReceiver() {

            private lateinit var nManager: NotificationManager
            private lateinit var nChannel: NotificationChannel
            private lateinit var nBuilder: Notification.Builder
            private val nChannelId = "Timer Notification"
            private val nDescription = "Notification for when the timer expires"
            private lateinit var mediaPlayer: MediaPlayer

            override fun onReceive(context: Context?, intent: Intent?) {
                nManager =
                    context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val playAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                mediaPlayer = MediaPlayer.create(context, playAlarmSound)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nChannel = NotificationChannel(
                        nChannelId,
                        nDescription,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    nChannel.enableLights(true)
                    nChannel.lightColor = Color.RED
                    nChannel.enableVibration(true)
                    val resultsIntent = Intent(context, TimerActivity::class.java)
                    val resultPendingIntent = PendingIntent.getActivity(context, 2, resultsIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    nManager.createNotificationChannel(nChannel)
                    nBuilder = Notification.Builder(context, nChannelId)
                        .setContentTitle("Timer!")
                        .setContentText("Your Timer has Expired")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true)
                } else {
                    val resultsIntent = Intent(context, TimerActivity::class.java)
                    val resultPendingIntent = PendingIntent.getActivity(context, 2, resultsIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    nBuilder = Notification.Builder(context)
                        .setContentTitle("Timer!")
                        .setContentText("Your Timer has Expired")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true)
                }

                fun playAudio() {
                    val timer = object: CountDownTimer(10000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            mediaPlayer.start()
                            Log.d("TimerAudio", "Playing Audio for 10 seconds...")
                        }

                        override fun onFinish() {
                            mediaPlayer.release()
                            Log.d("TimerAudio", "Stopping Audio.")
                        }
                    }
                    timer.start()
                }
                nManager.notify(2, nBuilder.build())
                playAudio()
                Log.d("TimerActivity", "Timer Receiver: " + Date().toString())
            }
        }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            super.onCreateOptionsMenu(menu)
            menuInflater.inflate(R.menu.main_menu, menu)
            return true
        }

        override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
            menu?.getItem(1)?.isVisible = false
            return super.onPrepareOptionsMenu(menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            var menuSelected = ""

            when (item.itemId) {
                R.id.menuAlarm -> menuSelected = "Alarm"
                R.id.menuTimer -> menuSelected = "Timer"
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