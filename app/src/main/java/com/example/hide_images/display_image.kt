package com.example.hide_images

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.example.hide_images.adapter.Image_Adapter
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class display_image : AppCompatActivity() {
//    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)
    window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val restore_button = findViewById<ImageView>(R.id.restore)

        val imageView = findViewById<ImageView>(R.id.image)
        val imageuri = intent.getParcelableExtra<Uri>("image")
        Glide.with(this).load(imageuri).into(imageView)

        restore_button.setOnClickListener{

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val downloadsUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                TODO("VERSION.SDK_INT < Q")
            }

            downloadsUri?.let {
                this.contentResolver.openOutputStream(it)?.use { outputStream ->
                    this.contentResolver.openInputStream(imageuri!!)?.use { inputStream ->
                        copyStream(inputStream, outputStream)
                    }
                }
                try {
                    val deletefile = File(imageuri!!.path!!)
                    deletefile.delete()
                    Toast.makeText(this, "Image restored to Downloads.", Toast.LENGTH_SHORT).show()
                    finish()

                } catch (e:Exception) {
                    Log.e("ERROR", "ERROR WHILE DELETING", e)
                    Toast.makeText(this, "An error occurred while deleting the image.", Toast.LENGTH_SHORT).show()
                }
                 } ?: run {
                Toast.makeText(this , "Failed to move the image to Downloads.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyStream(input: InputStream?, output: OutputStream) {
        val buffer = ByteArray(1024)
        var length:Int
        while (input!!.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
    }
}