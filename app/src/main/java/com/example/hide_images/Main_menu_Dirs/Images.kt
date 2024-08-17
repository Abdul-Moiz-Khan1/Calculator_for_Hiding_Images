package com.example.hide_images.Main_menu_Dirs

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hide_images.R
import com.example.hide_images.adapter.Image_Adapter
import com.example.hide_images.databinding.ActivityImagesBinding
import com.example.hide_images.databinding.ActivityMainMenuBinding
import java.io.File

class Images : AppCompatActivity() {

    private val imagePath =
        File("/storage/emulated/0/Android/data/com.example.hide_images/files/.app_images")
    private lateinit var binding: ActivityImagesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityImagesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.noItem.visibility = View.INVISIBLE
        binding.noItemText.visibility = View.INVISIBLE
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)

        checkpermissions()
        binding.backButton.setOnClickListener {
            finish()
        }

        if (!imagePath.exists() || imagePath.listFiles()!!.isEmpty()) {
            binding.recView.visibility = View.GONE
            binding.noItem.visibility = View.VISIBLE
            binding.noItemText.visibility = View.VISIBLE
        } else {
            binding.recView.layoutManager = GridLayoutManager(this, 2)
            val imageFiles =
                imagePath.listFiles { file -> file.extension == "jpg" || file.extension == "png" || file.extension == "jpeg" }
            binding.recView.adapter = Image_Adapter(this, imageFiles)
        }

        binding.floatingActionButton.setOnClickListener {

        }

    }

    private fun checkpermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse("package:${this.applicationContext.packageName}")
                ContextCompat.startActivity(this, intent, null)
            } else {


            }
        }
    }
}