package com.example.basemusicapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basemusicapp.Adapter.AdapterSong
import com.example.basemusicapp.App.Companion.ACTION_NAME
import com.example.basemusicapp.App.Companion.ACTION_NEXT_SONG
import com.example.basemusicapp.App.Companion.ACTION_PAUSE_SONG
import com.example.basemusicapp.App.Companion.ACTION_PLAY_SONG
import com.example.basemusicapp.App.Companion.ACTION_PREVIOUS_SONG
import com.example.basemusicapp.App.Companion.ACTION_REPLAY_SONG
import com.example.basemusicapp.App.Companion.BROADCAST_ACTIONFILLTER
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.R
import com.example.basemusicapp.Service.ManagerPlayMusicService
import com.example.basemusicapp.api.EventPlay
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_song.*

class FragmentSong : Fragment() {

    lateinit var listSongs: ArrayList<Song>
    lateinit var listSongs_backUp: ArrayList<Song>
    lateinit var adapterSong: AdapterSong
    var isPlay: Boolean? = null
    var positon: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        listSongs = ArrayList()
        listSongs_backUp = ArrayList()
        getListCDByType()
        adapterSong = AdapterSong(requireContext(), listSongs,listSongs_backUp)
        return inflater.inflate(R.layout.fragment_song, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lv_song.apply {
            adapter = adapterSong
            layoutManager = LinearLayoutManager(requireContext())
        }
        avtiveSearchView()
    }


    private fun getListCDByType() {

        var database = Firebase.database
        var refReference: DatabaseReference = database.reference
        addPostEventListener(refReference)
    }

    private fun addPostEventListener(reference: DatabaseReference) {
        val postListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("ErrGetDataInAlbum", error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("ListMusic").children.forEach { snapshot ->
                    var id: Int = (snapshot.child("id").value.toString()).toInt()
                    var name: String = snapshot.child("name").value.toString()
                    var author: String = snapshot.child("author").value.toString()
                    var type: String = snapshot.child("type").value.toString()
                    var link: String = snapshot.child("link").value.toString()
                    var linkImg: String = snapshot.child("linkImg").value.toString()

                    var song = Song(id, name, author, type, link, linkImg)
                    listSongs.add(song)
                    listSongs_backUp.add(song)

                }
                adapterSong.notifyDataSetChanged()
            }
        }
        reference.addValueEventListener(postListener)
    }

    private fun avtiveSearchView(){

        searchView_songs.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterSong.filter.filter(newText)
                return false
            }

        })
    }

}