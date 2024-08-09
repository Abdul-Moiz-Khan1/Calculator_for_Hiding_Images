package com.example.hide_images

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class hidden_images : AppCompatActivity() {
    private var read_permission: Boolean = false
    private var write_permission: Boolean = false
    private lateinit var select: Button
    private lateinit var uri_list:ArrayList<Uri>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_images)

        select = findViewById(R.id.button2)
        val key = generatekey()
//        encrypt_images()
//        save_image()

        check_permissions()
        select.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            }
            startActivityForResult(intent, 1001)
        }
    }

    private fun check_permissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1001
            )
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1001
            )
        }
        if ((ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            read_permission = true
            write_permission = true
        } else {
            check_permissions()
        }


    }

    private fun save_image(encryptedimage: ByteArray, outputfile: File) {
        outputfile.writeBytes(encryptedimage)

    }

    private fun encrypt_images(image: File, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val imagebytes = Files.readAllBytes(image.toPath())
        return cipher.doFinal(imagebytes)


    }

    private fun generatekey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
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
        hide_images()
    }

    private fun hide_images() {
        val newDir = File(getExternalFilesDir(null), ".hidden")
        if(!newDir.exists()){
            newDir.mkdirs()
        }
            for(i in 0 until uri_list.size){
                val input_stram = contentResolver.openInputStream(uri_list[i])
                val newFile = File(newDir , "image_${System.currentTimeMillis()}.jpg")
                input_stram.use { input->
                    FileOutputStream(newFile).use {
                        output->
                        input?.copyTo(output)
                        contentResolver.delete(uri_list[i],null,null)
                    }
                }
            }

    }
}


