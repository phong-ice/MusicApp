package com.example.basemusicapp.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.basemusicapp.App.Companion.ACTION_NAME
import com.example.basemusicapp.App.Companion.BROADCAST_ACTIONFILLTER

class PassEventNotification() : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            var action = intent.action
            var intent = Intent(BROADCAST_ACTIONFILLTER)
            intent.putExtra(ACTION_NAME, action)
            context?.sendBroadcast(intent)
            Log.i("action",action.toString())
        }
    }

}