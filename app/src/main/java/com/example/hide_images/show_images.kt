package com.example.hide_images

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class show_images : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_images)

        val uri_list = intent.getParcelableArrayListExtra<Uri>("uri_list")
        if(uri_list.isNullOrEmpty()){
            Log.d("null? list" , "ahh shit null exception again")
        }
        for(i in 0 until uri_list!!.size){
            Log.d("uri list" , uri_list[i].toString())
        }
    }

}