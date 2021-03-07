package com.example.basemusicapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log

class App():Application(){

    companion object{
        public val CHANNEL_ID = "baseAppMusic"
        public val BROADCAST_ACTIONFILLTER = "broatcast_actionfillter"
        public val ACTION_NAME = "action_name"
        public val ACTION_NEXT_SONG = "next_song"
        public val ACTION_PREVIOUS_SONG = "previous_song"
        public val ACTION_PAUSE_SONG = "pause_song"
        public val ACTION_PLAY_SONG = "play_song"
        public val ACTION_REPLAY_SONG = "replay_song"
    }

    override fun onCreate() {
        super.onCreate()
        _createNotificationChannel()
    }

    private fun  _createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var serviceChannel = NotificationChannel(CHANNEL_ID,"phongice",NotificationManager.IMPORTANCE_DEFAULT)
            var notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

}