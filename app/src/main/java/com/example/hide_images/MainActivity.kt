package com.example.hide_images

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hide_images.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var expression: Expression
    private var decimal = true
    private var operator = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)


        val btn = findViewById<Button>(R.id.equal)
        btn.setOnClickListener{
            val code = findViewById<TextView>(R.id.calculation)
            val code1 = code.text.toString()

            if(code1 == "1313"){
                startActivity(Intent(this,hidden_images::class.java))
            }
        }

    }
    fun ClearAll(view: View) {
        binding.result.text = ""
        binding.calculation.text = ""
    }

    fun Backspace(view: View) {
        binding.calculation.text = binding.calculation.text.toString().dropLast(1)
        try {
            val lastchar = binding.calculation.text.toString().last()
            if (lastchar.isDigit()) {
                onEqual()
            }
        }catch (ex : Exception){
            Log.e("Last Char Error" , ex.toString())
        }
    }


    fun NumberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (decimal) {
                    binding.calculation.append(view.text)
                    decimal = false
                }
            } else
                binding.calculation.append(view.text)
            operator = true
        }
        onEqual()
    }

    fun OperatorAction(view: View) {
        if (view is Button && operator) {
            binding.calculation.append(view.text)
            operator = false
            decimal = true
        }
    }

    fun Equal(view: View) {
        onEqual()
    }

    fun onEqual(){
        if(decimal && operator){
            val txt = binding.calculation.text.toString()
            expression = ExpressionBuilder(txt).build()
            try{
                val resultfin = expression.evaluate()
                binding.result.text = resultfin.toString()
            }catch (ex: ArithmeticException){
                Log.e("Evaluation Error" , ex.toString())
                binding.result.text = "Error"

            }
        }
    }
}