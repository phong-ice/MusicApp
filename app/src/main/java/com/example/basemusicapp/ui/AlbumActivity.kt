package com.example.basemusicapp.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AlbumActivity : AppCompatActivity() {

    lateinit var listSongs:ArrayList<Song>
    lateinit var getType:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        listSongs = ArrayList()
    }

    private fun getListCDByType() {
        getType = intent.getStringExtra("type").toString()

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

                    if (type.contains(getType)) {
                        var song = Song(id, name, author,type,link,linkImg)
                        listSongs.add(song)
                    }

                }
//                adapterCD.notifyDataSetChanged()
            }
        }
        reference.addValueEventListener(postListener)
    }

}