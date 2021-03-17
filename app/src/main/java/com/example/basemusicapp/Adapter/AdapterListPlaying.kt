package com.example.basemusicapp.Adapter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.basemusicapp.App.Companion.ACTION_PLAY_SONG
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.R
import com.example.basemusicapp.Service.ManagerPlayMusicService
import kotlinx.android.synthetic.main.item_list_song_playing.view.*

class AdapterListPlaying(var context: Context, var listSong: ArrayList<Song>?, var position: Int) :
    RecyclerView.Adapter<AdapterListPlaying.ViewHolder>() {

    private var song: Song? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_name = view.tv_name_item_list_playing
        var tv_author = view.tv_author_item_list_playing
        var btn_delete = view.btn_delete_item_list_playing
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_song_playing, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listSong?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tv_name.text = listSong?.get(pos)?.name
        holder.tv_author.text = listSong?.get(pos)?.author
        if (pos == position) {
            holder.btn_delete.setImageResource(R.drawable.song_icon)
        } else {
            holder.btn_delete.setImageResource(R.drawable.ic_baseline_close_24)
        }
        holder.btn_delete.setOnClickListener {
            listSong?.removeAt(pos)
            notifyDataSetChanged()
        }
        holder.itemView.setOnClickListener {
            holder.btn_delete.setImageResource(R.drawable.song_icon)
            notifyDataSetChanged()
            context.stopService(Intent(context, ManagerPlayMusicService::class.java))
            context.startService(
                Intent(context, ManagerPlayMusicService::class.java)
                    .setAction(ACTION_PLAY_SONG)
                    .putExtra("pos", pos)
                    .putExtra("song", listSong?.get(pos))
            )
        }
    }

}