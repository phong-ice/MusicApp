package com.example.basemusicapp.Service

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.basemusicapp.App.Companion.ACTION_NAME
import com.example.basemusicapp.App.Companion.ACTION_NEXT_SONG
import com.example.basemusicapp.App.Companion.ACTION_PAUSE_SONG
import com.example.basemusicapp.App.Companion.ACTION_PLAY_SONG
import com.example.basemusicapp.App.Companion.ACTION_PREVIOUS_SONG
import com.example.basemusicapp.App.Companion.ACTION_REPLAY_SONG
import com.example.basemusicapp.App.Companion.BROADCAST_ACTIONFILLTER
import com.example.basemusicapp.App.Companion.CHANNEL_ID
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.R
import com.example.basemusicapp.ui.PlaySongActivity


class ManagerPlayMusicService() : Service() {

    lateinit var mediaPlayer: MediaPlayer
    var isPlay: Boolean? = null
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    var postion: Int = 0

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        if (intent != null) {
            var song:Song? = intent.getParcelableExtra("song")
            var action = intent.action
            postion = intent.getIntExtra("pos",888)
//            var createNotification = CreateNotification(baseContext,songs)

            when (action) {

                ACTION_PLAY_SONG -> {
                    mediaPlayer = MediaPlayer.create(baseContext, Uri.parse(song?.link))
                    mediaPlayer.start()
                    isPlay = mediaPlayer.isPlaying
                }
                ACTION_PAUSE_SONG -> {
                    mediaPlayer.pause()
                    isPlay = mediaPlayer.isPlaying
                }

                ACTION_REPLAY_SONG -> {
                    mediaPlayer.start()
                    isPlay = mediaPlayer.isPlaying
                }
            }
            handler = Handler(Looper.getMainLooper())

            runnable = Runnable {
                sendBroadcast(
                    Intent("Push_IsPlay")
                        .putExtra("isPlay", isPlay!!)
                        .putExtra("duration", mediaPlayer.duration)
                        .putExtra("currentPosition", mediaPlayer.currentPosition)
                        .putExtra("pos",postion)
                        .putExtra("song",song)
                )
                handler?.postDelayed(runnable, 1000)
            }
//        handler.removeCallbacks(runnable)
            handler?.postDelayed(runnable, 1000)

            baseContext.registerReceiver(broadcastReceiver_SeekBar, IntentFilter("Seekbar_seekTo"))
        }
        return START_NOT_STICKY

    }

    var broadcastReceiver_SeekBar = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                var progress = intent.getIntExtra("progress", 0)
                mediaPlayer.seekTo(progress)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        handler.removeCallbacks(runnable)
        unregisterReceiver(broadcastReceiver_SeekBar)
    }
}