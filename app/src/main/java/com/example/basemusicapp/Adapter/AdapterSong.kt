package com.example.basemusicapp.Adapter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basemusicapp.App.Companion.ACTION_NAME
import com.example.basemusicapp.App.Companion.ACTION_PLAY_SONG
import com.example.basemusicapp.App.Companion.BROADCAST_ACTIONFILLTER
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.R
import com.example.basemusicapp.Service.CreateNotification
import com.example.basemusicapp.Service.ManagerPlayMusicService
import com.example.basemusicapp.ui.PlaySongActivity
import kotlinx.android.synthetic.main.item_song.view.*

class AdapterSong(
    var context: Context,
    var listSong: ArrayList<Song>,
    var listSong_backUp: ArrayList<Song>
) :
    RecyclerView.Adapter<AdapterSong.ViewHolder>(), Filterable {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img = view.img_itemSong
        var tv_name = view.tv_nameSong_itemSong
        var tv_author = view.tv_authorSong_itemSong
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context.registerReceiver(broadcastReceiver, IntentFilter("MinimizeOrNoti"))
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load(listSong[position].linkImg)
            .into(holder.img)

        holder.tv_author.text = listSong[position].author
        holder.tv_name.text = listSong[position].name

//        var createNotification = CreateNotification(context, listSong[position])
        holder.itemView.setOnClickListener {

            context.stopService(Intent(context, ManagerPlayMusicService::class.java))
            context.startService(
                Intent(context, ManagerPlayMusicService::class.java)
                    .setAction(ACTION_PLAY_SONG)
                    .putExtra("pos", position)
                    .putExtra("song", listSong.get(position))
            )

//
            var createNotification = CreateNotification(context,listSong?.get(position)!!,position)
            createNotification._createNotification()

            var intent = Intent(context, PlaySongActivity::class.java)
                .putExtra("listSong", listSong).putExtra("pos", position)
            context.startActivity(intent)


        }
    }

    override fun getFilter(): Filter {
        return search_songs
    }

    private var search_songs = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {

            var listSong_Filter: ArrayList<Song> = ArrayList()

            if (constraint == null || constraint.length == 0) {

                listSong_Filter.addAll(listSong_backUp)

            } else {
                var searchPattern = constraint.toString().toLowerCase().trim()
                for (item in listSong) {
                    if (item.name.toLowerCase().contains(searchPattern) || item.author.contains(
                            searchPattern
                        )
                    ) {
                        listSong_Filter.add(item)
                    }
                }
            }

            var results = FilterResults()
            results.values = listSong_Filter

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            listSong.clear()
            listSong.addAll(results?.values as ArrayList<Song>)
            notifyDataSetChanged()
        }
    }

    var broadcastReceiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            var pos = intent?.getIntExtra("pos",888)
            context?.startActivity(Intent(context,PlaySongActivity::class.java)
                .putExtra("pos",pos)
                .putExtra("listSong",listSong))
        }

    }
}