package com.example.medshelf.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.S
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.medshelf.R

private const val CHANNEL_ID = "medshelf_medical_alarm_channel"
private const val ACTION_STOP = "STOP_MEDSHELF_ALARM"
private const val FIVE_MINUTES = 5 * 60 * 1000L
private const val NOTIFICATION_ID = 999

class ReminderAlarmService : Service() {

    private var vibrator: Vibrator? = null
    private var ringtone: Ringtone? = null
    private val handler = Handler(Looper.getMainLooper())

    private val stopRunnable = Runnable {
        stopAlarm()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopAlarm()
            return START_NOT_STICKY
        }

        val title = intent?.getStringExtra("title") ?: "MedShelf Reminder"
        val message = intent?.getStringExtra("message") ?: "You have a medical reminder."

        createChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Stop Alert",
                createStopPendingIntent()
            )
            .build()

        if (SDK_INT >= 34) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        startVibration()
        startSound()

        handler.removeCallbacks(stopRunnable)
        handler.postDelayed(stopRunnable, FIVE_MINUTES)

        return START_NOT_STICKY
    }

    private fun createStopPendingIntent(): PendingIntent {
        val stopIntent = Intent(this, ReminderAlarmService::class.java).apply {
            action = ACTION_STOP
        }

        return PendingIntent.getService(
            this,
            1001,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun startVibration() {
        vibrator?.cancel() // Stop any previous vibration

        vibrator = if (SDK_INT >= S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1500, 700)

        if (SDK_INT >= O) {
            val effect = VibrationEffect.createWaveform(pattern, 0)
            if (SDK_INT >= 33) {
                val attributes = VibrationAttributes.Builder()
                    .setUsage(VibrationAttributes.USAGE_ALARM)
                    .build()
                vibrator?.vibrate(effect, attributes)
            } else {
                vibrator?.vibrate(effect)
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun startSound() {
        ringtone?.stop() // Stop any previous sound

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val ringerMode = audioManager.ringerMode

        // Only play sound if NOT in Silent or Vibrate mode
        if (ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            ringtone = RingtoneManager.getRingtone(this, alarmUri)

            // Set to USAGE_NOTIFICATION so it respects notification volume and silent mode
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            ringtone?.audioAttributes = attributes

            ringtone?.play()
        }
    }

    private fun stopAlarm() {
        handler.removeCallbacks(stopRunnable)
        vibrator?.cancel()
        ringtone?.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createChannel() {
        if (SDK_INT >= O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MedShelf Medical Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Long medical reminder alerts"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500)
                setSound(null, null)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(stopRunnable)
        vibrator?.cancel()
        ringtone?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}