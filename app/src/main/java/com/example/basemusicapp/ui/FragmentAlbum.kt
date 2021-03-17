package com.example.basemusicapp.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basemusicapp.Adapter.AdapterAlbum
import com.example.basemusicapp.Model.Album
import com.example.basemusicapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_albums.*

class FragmentAlbum : Fragment() {

    lateinit var listAblum:ArrayList<Album>
    lateinit var listAblum_backUp:ArrayList<Album>
    lateinit var adapterAlbum:AdapterAlbum


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAblum = ArrayList()
        listAblum_backUp = ArrayList()
        adapterAlbum = AdapterAlbum(requireContext(),listAblum,listAblum_backUp)

        lv_album.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = adapterAlbum
        }
        val database = Firebase.database
        val databaseReference = database.reference
        addPostEventListener(databaseReference)

        active_searchView()
    }


    fun addPostEventListener(postReferent: DatabaseReference) {

        val postListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("errReadDataFirebase", error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.child("Category").children.forEach { snapshot ->
                    var id: Int = (snapshot.key.toString()).toInt()
                    var linkImg = snapshot.child("linkImg").value.toString()
                    var nameAlbum = snapshot.child("name").value.toString()
                    var album = Album(id, nameAlbum,linkImg)
                    listAblum.add(album)
                    listAblum_backUp.add(album)
                }

        adapterAlbum.notifyDataSetChanged()
            }

        }
        postReferent.addValueEventListener(postListener)

    }

    fun active_searchView(){
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                adapterAlbum.filter.filter(newText)

                return false
            }

        })
    }

}