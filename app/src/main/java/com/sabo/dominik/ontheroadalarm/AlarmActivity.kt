package com.sabo.dominik.ontheroadalarm

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sabo.dominik.ontheroadalarm.databinding.ActivityAlarmBinding
import java.io.IOException


class AlarmActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: WakeLock? = null
    private lateinit var ringtone: Uri
    private lateinit var binding: ActivityAlarmBinding
    private val repository: AlarmRepository = AlarmRepository.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm)

        if(repository.alarms.isEmpty()) repository.loadData(this)

        val position = intent.getIntExtra("position", 0)

        ringtone = Uri.parse(repository.alarms[position].ringtone)
        repository.alarms[position].toggle()
        mediaPlayer = MediaPlayer()

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
            this.javaClass.simpleName
        )
        wakeLock!!.acquire(10*60*1000L /*10 minutes*/)

        try {
            playAlarm(ringtone, mediaPlayer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        binding.tvArrived.text = repository.alarms[position].name

        binding.btnStop.setOnClickListener(){
            mediaPlayer!!.stop();
            mediaPlayer!!.release();
            wakeLock!!.release();
            repository.saveData(this)
            finish()
        }

    }

    private fun playAlarm(ringtone: Uri, mediaPlayer: MediaPlayer?) {
        val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()

        mediaPlayer!!.reset()
        mediaPlayer.setDataSource(this@AlarmActivity, ringtone)
        mediaPlayer.setAudioAttributes(audioAttributes)
        mediaPlayer.isLooping = true
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
}