@file:Suppress("DEPRECATION")

package com.example.hide_images.main_menu_acts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hide_images.R
import com.example.hide_images.adapter.Image_Adapter
import com.example.hide_images.databinding.ActivityImagesBinding
import kotlinx.coroutines.selects.select
import java.io.File
import java.io.FileOutputStream

class Images : AppCompatActivity() {
    private var read_permission: Boolean = false
    private var write_permission: Boolean = false
    private var uri_list: ArrayList<Uri> = ArrayList()

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
        binding.floatingActionButton.setOnClickListener {
            checkpermissions()
            if (read_permission && write_permission) {
                selectimages()
            } else {
                checkpermissions()
            }

        }

        if (!imagePath.exists() || imagePath.listFiles()!!.isEmpty()) {
            binding.recView.visibility = View.GONE
            binding.noItem.visibility = View.VISIBLE
            binding.noItemText.visibility = View.VISIBLE
        } else {
            binding.recView.layoutManager = GridLayoutManager(this, 3)
            val imageFiles =
                imagePath.listFiles { file -> file.extension == "jpg" || file.extension == "png" || file.extension == "jpeg" }
            binding.recView.adapter = Image_Adapter(this, imageFiles)
        }


    }

    private fun selectimages() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, 1001)

    }

    private fun checkpermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse("package:${this.applicationContext.packageName}")
                ContextCompat.startActivity(this, intent, null)
            } else {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        1001
                    )
                }
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    read_permission = true
                    write_permission = true
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("code", requestCode.toString())
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            data?.let { intent_data ->
                val clip = intent_data.clipData
                if (clip != null) {
                    for (i in 0 until clip.itemCount) {
                        Log.d("uris", clip.getItemAt(i).uri.toString())
                        uri_list.add(clip.getItemAt(i).uri)
                        if (uri_list.size == intent_data.clipData!!.itemCount) {
                            hide_images()
                        }
                    }
                    Log.d("count?", clip.itemCount.toString())
                } else {
                    intent_data.data?.let { uri_list.add(it) }
                    Log.d("count?", "1")
                }
            }
        } else {
            Log.d("null?", resultCode.toString())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun hide_images() {
        binding.recView.adapter!!.notifyDataSetChanged()
        val newDir = File(getExternalFilesDir(null), ".app_images")
        if (!newDir.exists()) {
            Log.d("dirs", "making new ")
            newDir.mkdirs()
        }
        Log.d("loop", uri_list.size.toString())
        for (i in 0 until uri_list.size) {
            val input_stram = contentResolver.openInputStream(uri_list[i])
            val newFile = File(newDir, "image_${System.currentTimeMillis()}.jpg")
            Log.d("loop", i.toString())
            input_stram.use { input ->
                FileOutputStream(newFile).use { output ->
                    input?.copyTo(output)
                    try {
                        DocumentFile.fromSingleUri(this, uri_list[i])?.delete()
                        Log.d("del", uri_list[i].toString())
//                        Toast.makeText(this , "${uri_list.size} images hidden", Toast.LENGTH_SHORT).show()
                    } catch (
                        e: Exception
                    ) {
                        Log.e("eRROR", "dELETION error", e)
                    }
                }
            }
        }
        binding.recView.adapter!!.notifyDataSetChanged()
    }
}