package com.example.market

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView

class SignUpActivity : AppCompatActivity() {

    val back: MaterialTextView by lazy {
        findViewById<MaterialTextView>(R.id.textViewLoginLink)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        back.setOnClickListener {
            // LoginActivity로 이동
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // 현재 SignUpActivity 종료
        }
    }
}
