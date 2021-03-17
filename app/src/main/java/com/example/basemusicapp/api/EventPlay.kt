package com.example.basemusicapp.api

import android.content.Context
import android.content.Intent
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.Service.ManagerPlayMusicService

interface EventPlay {
    fun onPlaySong()
    fun onPauseSong()
    fun onNextSong()
    fun onReplaySong()
    fun onPreviousSong()
}