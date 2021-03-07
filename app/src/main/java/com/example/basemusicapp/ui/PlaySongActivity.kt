package com.example.basemusicapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.basemusicapp.App.Companion.ACTION_NAME
import com.example.basemusicapp.App.Companion.ACTION_NEXT_SONG
import com.example.basemusicapp.App.Companion.ACTION_PAUSE_SONG
import com.example.basemusicapp.App.Companion.ACTION_PREVIOUS_SONG
import com.example.basemusicapp.App.Companion.ACTION_REPLAY_SONG
import com.example.basemusicapp.App.Companion.BROADCAST_ACTIONFILLTER
import com.example.basemusicapp.R
import kotlinx.android.synthetic.main.activity_play_song.*


class PlaySongActivity : AppCompatActivity() {

    var isPlay: Boolean? = null
    var position: Int = 0
    var name: String = ""
    var author: String = ""
    var _process: Int = 0
    var linkImg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_song)
        linkImg = intent.getStringExtra("Imglink").toString()
        addEvent()
        position = intent.getIntExtra("pos", 999)


        registerReceiver(broadcastReceiver, IntentFilter("Push_IsPlay"))
    }

    private fun addEvent() {
        Glide.with(this).load(linkImg).into(img_PlaySong)

        btn_backPlaySong.setOnClickListener {
            finish()
        }
        btn_play_PlaySong.setOnClickListener {
            if (isPlay == true) {
                var intentBroadcast = Intent(BROADCAST_ACTIONFILLTER)
                    .putExtra("pos", position)
                    .putExtra(ACTION_NAME, ACTION_PAUSE_SONG)
                sendBroadcast(intentBroadcast)
                btn_play_PlaySong.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            } else if (isPlay == false) {
                var intentBroadcast = Intent(BROADCAST_ACTIONFILLTER)
                    .putExtra("pos", position)
                    .putExtra(ACTION_NAME, ACTION_REPLAY_SONG)
                sendBroadcast(intentBroadcast)
                btn_play_PlaySong.setImageResource(R.drawable.ic_baseline_pause_24)
            }

        }

        btn_next_PlaySong.setOnClickListener {
            position++
            var intentBroadcast = Intent(BROADCAST_ACTIONFILLTER)
                .putExtra("pos", position)
                .putExtra(ACTION_NAME, ACTION_NEXT_SONG)
            sendBroadcast(intentBroadcast)
        }

        btn_pre_PlaySong.setOnClickListener {
            position--
            var intentBroadcast = Intent(BROADCAST_ACTIONFILLTER)
                .putExtra("pos", position)
                .putExtra(ACTION_NAME, ACTION_PREVIOUS_SONG)
            sendBroadcast(intentBroadcast)
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
    }

    var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                isPlay = intent.getBooleanExtra("isPlay", true)
                var _name = intent.getStringExtra("name").toString()
                var _author = intent.getStringExtra("author").toString()
                var duration = intent.getIntExtra("duration", 0)
                var currentPosition = intent.getIntExtra("currentPosition", 0)
                if (!name.equals(_name)) {
                    tv_namePlaySong.text = _name
                    tv_authorPlaySong.text = _author
                    name = _name
                    author = _author
                }

                seekBar.max = duration
                seekBar.progress = currentPosition

                Log.i("isPlay", _name)
            }
        }

    }
}