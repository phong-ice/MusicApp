package com.example.basemusicapp.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    var id:Int,
    var name:String,
    var author:String,
    var type:String,
    var link:String,
    var linkImg:String
) : Parcelable