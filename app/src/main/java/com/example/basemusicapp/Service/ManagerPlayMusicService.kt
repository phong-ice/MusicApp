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
import com.example.basemusicapp.R
import com.example.basemusicapp.ui.PlaySongActivity


class ManagerPlayMusicService() : Service() {

    lateinit var mediaPlayer: MediaPlayer
    var isPlay: Boolean? = null
    lateinit var name: String
    lateinit var author: String
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    var drawable_play: Int = 0
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
            var link = intent.getStringExtra("link")
            name = intent.getStringExtra("name").toString()
            author = intent.getStringExtra("author").toString()
            drawable_play =
                intent.getIntExtra("drawable_play", R.drawable.ic_baseline_play_arrow_24)
            postion = intent.getIntExtra("pos", 999)
            var action = intent.action

            Log.i("postion", postion.toString())

            when (action) {

                ACTION_PLAY_SONG -> {
                    mediaPlayer = MediaPlayer.create(baseContext, Uri.parse(link))
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
            CreateNotification(flags)
            handler = Handler(Looper.getMainLooper())

            runnable = Runnable {
                sendBroadcast(
                    Intent("Push_IsPlay")
                        .putExtra("isPlay", isPlay!!)
                        .putExtra("name", name)
                        .putExtra("author", author)
                        .putExtra("duration", mediaPlayer.duration)
                        .putExtra("currentPosition", mediaPlayer.currentPosition)
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

    private fun CreateNotification(flags: Int) {
        var notificationManagerCompat: NotificationManagerCompat =
            NotificationManagerCompat.from(baseContext)

        //go PlaySong Screen
        var intent = Intent(baseContext, PlaySongActivity::class.java)
        var pendingIntent =
            PendingIntent.getActivity(baseContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        //send event next Song
        var drawable_nextSong = R.drawable.ic_baseline_skip_next_24
        var sendEventNext = Intent(BROADCAST_ACTIONFILLTER)
            .putExtra(ACTION_NAME, ACTION_NEXT_SONG)
            .putExtra("pos", (postion + 1))
        var pendingIntent_eventNext =
            PendingIntent.getBroadcast(
                baseContext,
                0,
                sendEventNext,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        //send event play Song
        var sendEventPlay = Intent(BROADCAST_ACTIONFILLTER)
        if (!mediaPlayer.isPlaying) {
            sendEventPlay.putExtra(ACTION_NAME, ACTION_REPLAY_SONG)
                .putExtra("pos", postion)
        } else {
            sendEventPlay.putExtra(ACTION_NAME, ACTION_PLAY_SONG)
                .putExtra("pos", postion)
        }
        var pendingIntent_eventPlay =
            PendingIntent.getBroadcast(
                baseContext,
                0,
                sendEventPlay,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        //send event previous Song

        var drawable_preSong = R.drawable.ic_baseline_skip_previous_24
        var sendEventPre = Intent(BROADCAST_ACTIONFILLTER)
            .putExtra(ACTION_NAME, ACTION_PREVIOUS_SONG)
            .putExtra("pos", (postion - 1))
        var pendingIntent_eventPre = PendingIntent.getBroadcast(
            baseContext,
            0,
            sendEventPre,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        var bitmap =
            BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_sports_basketball_24)

        var notification = NotificationCompat.Builder(baseContext, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_avt)
            .setContentText(name)
            .setContentTitle(author)
            .setLargeIcon(bitmap)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .addAction(drawable_preSong, "ACTION_PREVIOUS_SONG", pendingIntent_eventPre)
            .addAction(drawable_play, "ACTION_PLAY_SONG", pendingIntent_eventPlay)
            .addAction(drawable_nextSong, "ACTION_NEXT_SONG", pendingIntent_eventNext)
            .addAction(drawable_nextSong, "ACTION_NEXT_SONG", pendingIntent_eventNext)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManagerCompat.notify(1, notification)
    }

}