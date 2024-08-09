package com.example.hide_images

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val code = findViewById<EditText>(R.id.editTextText).toString()
        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener{
            if(code.equals("1313")){
                startActivity(Intent(this,hidden_images::class.java))
            }else{
                Toast.makeText(this , "Wrong Code", Toast.LENGTH_SHORT).show()
            }
        }

    }
}