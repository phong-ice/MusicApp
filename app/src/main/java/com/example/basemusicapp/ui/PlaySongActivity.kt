package com.example.basemusicapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.basemusicapp.Adapter.AdapterListPlaying
import com.example.basemusicapp.App
import com.example.basemusicapp.App.Companion.ACTION_NAME
import com.example.basemusicapp.App.Companion.ACTION_NEXT_SONG
import com.example.basemusicapp.App.Companion.ACTION_PAUSE_SONG
import com.example.basemusicapp.App.Companion.ACTION_PLAY_SONG
import com.example.basemusicapp.App.Companion.ACTION_PREVIOUS_SONG
import com.example.basemusicapp.App.Companion.ACTION_REPLAY_SONG
import com.example.basemusicapp.App.Companion.BROADCAST_ACTIONFILLTER
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.R
import com.example.basemusicapp.Service.CreateNotification
import com.example.basemusicapp.Service.ManagerPlayMusicService
import com.example.basemusicapp.api.EventPlay
import com.example.basemusicapp.api.PlaySongPresenter
import com.google.firebase.inject.Deferred
import kotlinx.android.synthetic.main.activity_play_song.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL


class PlaySongActivity : AppCompatActivity(), EventPlay {

    var isPlay: Boolean = true
    var position: Int = 0
    var _process: Int = 0
    private var listSongs: ArrayList<Song>? = ArrayList()
    lateinit var adapterPlaying: AdapterListPlaying

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_song)
        listSongs = intent.getParcelableArrayListExtra("listSong")
        position = intent.getIntExtra("pos", 999)
        addEvent()
        registerReceiver(broadcastReceiver, IntentFilter("Push_IsPlay"))
        registerReceiver(broadcastEventPlaying, IntentFilter(BROADCAST_ACTIONFILLTER))
        initListPlaying()
    }

    private fun addEvent() {
        btn_backPlaySong.setOnClickListener {
           startActivity(Intent(this,Content::class.java))
        }
        btn_play_PlaySong.setOnClickListener {
            if (isPlay) {
                onPauseSong()
            } else {
                onReplaySong()
            }
        }

        btn_next_PlaySong.setOnClickListener {
            onNextSong()
        }

        btn_pre_PlaySong.setOnClickListener {
            onPreviousSong()

        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                _process = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                unregisterReceiver(broadcastReceiver)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                var intent = Intent("Seekbar_seekTo")
                intent.putExtra("progress", _process)
                sendBroadcast(intent)
                registerReceiver(broadcastReceiver, IntentFilter("Push_IsPlay"))
            }
        })

        btn_showListSong.setOnClickListener {
            layout_list_playing.visibility = View.VISIBLE
        }
        btn_close_layout_playing.setOnClickListener {
            layout_list_playing.visibility = View.GONE
        }
    }

    var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                isPlay = intent.getBooleanExtra("isPlay", true)
                var duration = intent.getIntExtra("duration", 0)
                var currentPosition = intent.getIntExtra("currentPosition", 0)
                position = intent.getIntExtra("pos", 888)

                Glide.with(baseContext).load(listSongs?.get(position)?.linkImg).into(img_PlaySong)
                tv_namePlaySong.text = listSongs?.get(position)?.name
                tv_authorPlaySong.text = listSongs?.get(position)?.author

                if (isPlay) {
                    btn_play_PlaySong.setImageResource(R.drawable.ic_baseline_pause_24)
                } else {
                    btn_play_PlaySong.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
                if (currentPosition === duration) {
                    onNextSong()
                }
                seekBar.max = duration
                seekBar.progress = currentPosition
            }
        }

    }
    var broadcastEventPlaying = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                var action = intent.getStringExtra(ACTION_NAME)
                position = intent.getIntExtra("pos", 999)

                var playSongPresenter = PlaySongPresenter(this@PlaySongActivity)
                playSongPresenter.eventPlaySong(action!!)
            }
        }

    }

    private fun initListPlaying() {
        adapterPlaying = AdapterListPlaying(this, listSongs,position)
        lv_songPlaying.apply {
            layoutManager = LinearLayoutManager(this@PlaySongActivity)
            adapter = adapterPlaying
        }
    }

    override fun onPlaySong() {
        var createNotification = CreateNotification(this, listSongs?.get(position)!!, position)
        createNotification._createNotification()
        adapterPlaying.notifyDataSetChanged()
        stopService(Intent(this, ManagerPlayMusicService::class.java))
        var intent = Intent(this, ManagerPlayMusicService::class.java)
            .setAction(ACTION_PLAY_SONG)
            .putExtra("song", listSongs?.get(position))
            .putExtra("pos", position)
        startService(intent)
    }

    override fun onPauseSong() {
        var createNotification = CreateNotification(this, listSongs?.get(position)!!, position)
        createNotification._createNotification()
        adapterPlaying.notifyDataSetChanged()
        var intent = Intent(this, ManagerPlayMusicService::class.java)
            .setAction(ACTION_PAUSE_SONG)
            .putExtra("song", listSongs?.get(position))
            .putExtra("pos", position)
        startService(intent)
    }

    override fun onNextSong() {
        position++
        if (position <= listSongs?.size!! - 1) {
            stopService(Intent(this, ManagerPlayMusicService::class.java))
            var createNotification = CreateNotification(this, listSongs?.get(position)!!, position)
            createNotification._createNotification()
            adapterPlaying.notifyDataSetChanged()
            var intent = Intent(this, ManagerPlayMusicService::class.java)
                .setAction(ACTION_PLAY_SONG)
                .putExtra("song", listSongs?.get(position))
                .putExtra("pos", position)
            startService(intent)
        }
    }

    override fun onReplaySong() {
        var createNotification = CreateNotification(this, listSongs?.get(position)!!, position)
        createNotification._createNotification()
        adapterPlaying.notifyDataSetChanged()
        var intent = Intent(this, ManagerPlayMusicService::class.java)
            .setAction(ACTION_REPLAY_SONG)
            .putExtra("song", listSongs?.get(position))
            .putExtra("pos", position)
        startService(intent)
    }

    override fun onPreviousSong() {
        position--
        if (position >= 0) {
            stopService(Intent(this, ManagerPlayMusicService::class.java))
            var createNotification = CreateNotification(this, listSongs?.get(position)!!, position)
            createNotification._createNotification()
            adapterPlaying.notifyDataSetChanged()
            var intent = Intent(this, ManagerPlayMusicService::class.java)
                .setAction(ACTION_PLAY_SONG)
                .putExtra("song", listSongs?.get(position))
                .putExtra("pos", position)
            startService(intent)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, Content::class.java))
    }
}