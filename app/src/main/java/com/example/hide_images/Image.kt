package com.example.hide_images

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import java.io.InputStream
import java.io.OutputStream

class Image : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

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

            val downloadsUri = this.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            downloadsUri?.let {
                this.contentResolver.openOutputStream(it)?.use { outputStream ->
                    this.contentResolver.openInputStream(imageuri!!)?.use { inputStream ->
                        copyStream(inputStream, outputStream)
                    }
                }

                // Delete the original image after moving
                DocumentFile.fromSingleUri(this , imageuri!!)!!.delete()
                val rowsDeleted = this.contentResolver.delete(imageuri, null, null)
                if (rowsDeleted > 0) {
                    Toast.makeText(this , "Image moved to Downloads and original image deleted successfully.",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this , "Failed to delete the original image.",Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this , "Failed to move the image to Downloads.",Toast.LENGTH_SHORT).show()
            }

//            val inputStream = this.contentResolver.openInputStream(imageuri!!)
//            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//            val newimage = File(downloads, "image_${System.currentTimeMillis()}.jpg")
//
//            try{
//                inputStream.use { input->
//                    FileOutputStream(newimage).use{
//                        output->
//                        copyStream(input , output)
//                    }
//                this.contentResolver.delete(imageuri , null ,null)
//                }
//            }catch (e:Exception){
//                Log.e("ERROR" , "ERROR WHILE RESTORING" ,e)
//            }
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