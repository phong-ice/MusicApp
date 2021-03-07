package com.example.basemusicapp.api

interface EventPlay {
    fun onPlaySong(pos:Int)
    fun onPauseSong(pos:Int)
    fun onRePlaySong(pos:Int)
    fun onNextSong(pos:Int)
    fun onPreviousSong(pos:Int)
}