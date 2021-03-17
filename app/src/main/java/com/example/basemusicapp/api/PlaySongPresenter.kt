package com.example.basemusicapp.api

import android.util.Log
import com.example.basemusicapp.App.Companion.ACTION_NEXT_SONG
import com.example.basemusicapp.App.Companion.ACTION_PAUSE_SONG
import com.example.basemusicapp.App.Companion.ACTION_PLAY_SONG
import com.example.basemusicapp.App.Companion.ACTION_PREVIOUS_SONG
import com.example.basemusicapp.App.Companion.ACTION_REPLAY_SONG

class PlaySongPresenter(val eventPlay: EventPlay) {

    public fun eventPlaySong(action: String) {
        if (action == ACTION_PLAY_SONG) {
            eventPlay.onPlaySong()

        } else if (action == ACTION_REPLAY_SONG) {
            eventPlay.onReplaySong()

        } else if (action == ACTION_PAUSE_SONG) {
            eventPlay.onPauseSong()

        } else if (action == ACTION_NEXT_SONG) {
                eventPlay.onNextSong()

        } else if (action == ACTION_PREVIOUS_SONG) {
                eventPlay.onPreviousSong()
            }
        }
    }