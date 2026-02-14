package com.example.flamingcoding.androidTrials

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class TestBoardCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Test board cast", Toast.LENGTH_SHORT).show()
//        val action = intent?.action
//        if (action.equals("com.example.flamingcoding.androidTrials.TEST_BROADCAST")) {
//            Toast.makeText(context, "Test board cast", Toast.LENGTH_SHORT).show()
//        }
    }
}