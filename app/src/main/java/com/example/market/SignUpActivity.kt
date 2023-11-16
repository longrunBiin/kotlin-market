package com.example.market

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    val back: MaterialTextView by lazy {
        findViewById<MaterialTextView>(R.id.textViewLoginLink)
    }

    val sign_up: Button by lazy {
        findViewById<Button>(R.id.buttonSignUp)
    }

    val emailText: EditText by lazy {
        findViewById<EditText>(R.id.editTextEmail)
    }

    val passwordText: EditText by lazy {
        findViewById<EditText>(R.id.editTextPassword)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // firebase Authentication기능을 가져옴
        auth = Firebase.auth

        back.setOnClickListener {
            // LoginActivity로 이동
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // 현재 SignUpActivity 종료
        }

        sign_up.setOnClickListener{
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show()
                        //doLogin(email, password)

                    } else {
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
//        private fun doLogin(email: String, password: String) {
//            Firebase.auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { // it: Task<AuthResult!>
//                    if (it.isSuccessful) {
//                        startActivity(
//                            Intent(this, MainActivity::class.java)
//                        )
//                        finish()
//                    } else {
//                        Log.w("LoginActivity", "signInWithEmail", it.exception)
//                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }

    }
}
