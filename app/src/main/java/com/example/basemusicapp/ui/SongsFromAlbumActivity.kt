package com.example.basemusicapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.basemusicapp.Adapter.AdapterSong
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_songs_from_album.*

class SongsFromAlbumActivity : AppCompatActivity() {

    lateinit var nameAlbum:String
    lateinit var linkImg:String
    lateinit var listSongs:ArrayList<Song>
    lateinit var adapterSong: AdapterSong

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_songs_from_album)
        nameAlbum = intent.getStringExtra("nameAlbum").toString()
        linkImg = intent.getStringExtra("linkImg").toString()
        listSongs = ArrayList()
        adapterSong = AdapterSong(this,listSongs,listSongs)

        Glide.with(this).load(linkImg).into(img_SongsFromAlbum)
        lv_SongFromAlbum.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterSong
        }
        getListCDByType()
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

                   if (type.contains(nameAlbum)){
                       var song = Song(id, name, author, type, link, linkImg)
                       listSongs.add(song)
                   }

                }
                adapterSong.notifyDataSetChanged()
            }
        }
        reference.addValueEventListener(postListener)
    }

}