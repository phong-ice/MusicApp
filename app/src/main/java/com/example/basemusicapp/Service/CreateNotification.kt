package com.example.basemusicapp.Service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.session.MediaSession
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.basemusicapp.App.Companion.CHANNEL_ID
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.R
import com.example.basemusicapp.ui.PlaySongActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOError
import java.net.URL


class CreateNotification(
    var context: Context,
    var song: Song,
    var pos:Int
) {

    var bitmap: Bitmap? = null

    public fun _createNotification() {
        getBitMap()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent("MinimizeOrNoti").apply {
                putExtra("pos",pos)
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.song_icon)
                .setContentTitle(song.name)
                .setContentText(song.author)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            with(NotificationManagerCompat.from(context)) {
                notify(1, builder.build())
            }
        }
    }

    private fun getBitMap() {
        var url: URL = URL(song.linkImg)
        val result: kotlinx.coroutines.Deferred<Bitmap?> = GlobalScope.async {
            url.toBitmap()
        }
        GlobalScope.launch(Dispatchers.Main) {
            bitmap = result.await()
        }
    }

    fun URL.toBitmap(): Bitmap? {
        return try {
            BitmapFactory.decodeStream(openStream())
        } catch (err: IOError) {
            null
        }
    }
}