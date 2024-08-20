package com.example.hide_images

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hide_images.adapter.Image_Adapter
import java.io.File

class show_images : AppCompatActivity() {
    private val imagePath =
        File("/storage/emulated/0/Android/data/com.example.hide_images/files/.app_images")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_images)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val uri_list = intent.getParcelableArrayListExtra<Uri>("uri_list")
        if (uri_list.isNullOrEmpty()) {
            Log.d("null? list", "ahh shit null exception again")
        }
        for (i in 0 until uri_list!!.size) {
            Log.d("uri list", uri_list[i].toString())
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rec_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        val imageFiles =
            imagePath.listFiles { file -> file.extension == "jpg" || file.extension == "png" || file.extension == "jpeg" }
        recyclerView.adapter = Image_Adapter(this, imageFiles)

    }

}