package com.example.basemusicapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.basemusicapp.App
import com.example.basemusicapp.App.Companion.ACTION_NAME
import com.example.basemusicapp.App.Companion.ACTION_NEXT_SONG
import com.example.basemusicapp.App.Companion.ACTION_PAUSE_SONG
import com.example.basemusicapp.App.Companion.ACTION_PREVIOUS_SONG
import com.example.basemusicapp.App.Companion.ACTION_REPLAY_SONG
import com.example.basemusicapp.App.Companion.BROADCAST_ACTIONFILLTER
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.R
import com.example.basemusicapp.Service.ManagerPlayMusicService
import com.example.basemusicapp.api.EventPlay
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.activity_play_song.*

class Content : AppCompatActivity() {

    private var isPlay: Boolean = true
    private var position: Int = -1
    var timeOut:Long =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)

        setFragment(FragmentAlbum())

        navBottom_content.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navBottom_album -> {
                    setFragment(FragmentAlbum())
                    true
                }
                R.id.navBottom_mySongs -> {
                    setFragment(FragmentSong())
                    true
                }
            }
            true
        }
        eventMinimize()
        registerReceiver(broadcastReceiver, IntentFilter("Push_IsPlay"))
    }

    private fun eventMinimize() {
        layout_minimize_Content.setOnClickListener {
           sendBroadcast(Intent("MinimizeOrNoti"))
        }
        btn_preveous_minimize_Content.setOnClickListener {
            sendBroadcast(
                Intent(BROADCAST_ACTIONFILLTER)
                    .putExtra("pos", position)
                    .putExtra(ACTION_NAME, ACTION_PREVIOUS_SONG)
            )
        }
        btn_next_minimize_content.setOnClickListener {
            sendBroadcast(
                Intent(BROADCAST_ACTIONFILLTER)
                    .putExtra("pos", position)
                    .putExtra(ACTION_NAME, ACTION_NEXT_SONG)
            )
        }
        btn_play_minimize_content.setOnClickListener {
            if (isPlay) {
                sendBroadcast(
                    Intent(BROADCAST_ACTIONFILLTER)
                        .putExtra("pos", position)
                        .putExtra(ACTION_NAME, ACTION_PAUSE_SONG)
                )
            } else {
                sendBroadcast(
                    Intent(BROADCAST_ACTIONFILLTER)
                        .putExtra("pos", position)
                        .putExtra(ACTION_NAME, ACTION_REPLAY_SONG)
                )
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        var fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragment, fragment)
        fragmentTransition.commit()
    }

    var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                layout_minimize_Content.visibility = View.VISIBLE
                isPlay = intent.getBooleanExtra("isPlay", true)
                var song:Song? = intent.getParcelableExtra("song")
                position = intent.getIntExtra("pos", 999)
                if (isPlay) {
                    btn_play_minimize_content.setImageResource(R.drawable.ic_baseline_pause_24)
                } else {
                    btn_play_minimize_content.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
                tv_name_minimize_Content.text = "${song?.name} - ${song?.author}"
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        layout_minimize_Content.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (timeOut + 2000 > System.currentTimeMillis()){
            finish()
            System.exit(0)
            return
        }else{
            Toast.makeText(this, "Press back again to exits", Toast.LENGTH_SHORT).show()
            timeOut = System.currentTimeMillis()
        }

    }
}