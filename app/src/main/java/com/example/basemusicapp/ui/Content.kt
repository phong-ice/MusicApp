package com.example.basemusicapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.basemusicapp.R
import kotlinx.android.synthetic.main.activity_content.*

class Content : AppCompatActivity() {
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
    }

    private fun setFragment(fragment: Fragment) {
        var fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragment, fragment)
        fragmentTransition.commit()
    }
}