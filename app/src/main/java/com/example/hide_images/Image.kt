package com.example.hide_images

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class Image : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val restore_button = findViewById<ImageView>(R.id.restore)

        val imageView = findViewById<ImageView>(R.id.image)
        val imageuri = intent.getParcelableExtra<Uri>("image")
        Glide.with(this).load(imageuri).into(imageView)

        restore_button.setOnClickListener{
            val inputStream = this.contentResolver.openInputStream(imageuri!!)
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val newimage = File(downloads, "image_${System.currentTimeMillis()}.jpg")

            try{
                inputStream.use { input->
                    FileOutputStream(newimage).use{
                        output->
                        copyStream(input , output)
                    }
                }
            }catch (e:Exception){
                Log.e("ERROR" , "ERROR WHILE RESTORING" ,e)
            }
        }
    }

    private fun copyStream(input: InputStream?, output: FileOutputStream) {
        val buffer = ByteArray(1024)
        var length:Int
        while (input!!.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
    }
}