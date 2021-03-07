package com.example.basemusicapp.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basemusicapp.Model.Album
import com.example.basemusicapp.R
import com.example.basemusicapp.ui.SongsFromAlbumActivity
import kotlinx.android.synthetic.main.item_album.view.*

class AdapterAlbum(var context: Context, var listAlbum: ArrayList<Album>,  var listAlbum_BackUp: ArrayList<Album>) :
    RecyclerView.Adapter<AdapterAlbum.ViewHolder>(), Filterable {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img = view.img_itemAlbum
        var tv_name = view.tv_nameAlbum_itemAlbum
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        )
    }

    override fun getItemCount(): Int {

        return listAlbum.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_name.text = listAlbum[position].name
        Glide.with(context).load(listAlbum[position].linkImg).into(holder.img)
        holder.itemView.setOnClickListener {
            var intent = Intent(context, SongsFromAlbumActivity::class.java)
                .putExtra("nameAlbum", listAlbum[position].name)
                .putExtra("linkImg", listAlbum[position].linkImg)
            context.startActivity(intent)
        }
    }

    override fun getFilter(): Filter {
        return search_album
    }

    private var search_album = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var listAlbum_Fillter: ArrayList<Album> = ArrayList()

            if (constraint == null || constraint.length == 0) {
                listAlbum_Fillter.addAll(listAlbum_BackUp)
                Log.i("constraint",listAlbum_BackUp.size.toString())
            } else {
                var filterPattern = constraint.toString().toLowerCase().trim()

                for (item: Album in listAlbum) {
                    if (item.name.toLowerCase().contains(filterPattern)) {
                        listAlbum_Fillter.add(item)
                    }
                }
            }

            var result = FilterResults()
            result.values = listAlbum_Fillter
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            listAlbum.clear()
            listAlbum.addAll(results?.values as ArrayList<Album>)
            notifyDataSetChanged()
        }

    }


}