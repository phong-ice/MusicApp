package com.example.basemusicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.basemusicapp.Model.Song
import com.example.basemusicapp.Service.CreateNotification
import com.example.basemusicapp.ui.Content
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this,Content::class.java))
//        var song:Song = Song(1,"phongice","phongice","phongice","1312312","https://cdn.pixabay.com/photo/2020/03/14/00/23/building-4929371_1280.jpg")
//        var createNotification = CreateNotification(this,song)
//        btn_startService.setOnClickListener {
//            createNotification._createNotification()
        }
}