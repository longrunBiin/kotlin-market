package com.example.market

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {


    val userEmail: EditText by lazy {
        findViewById(R.id.email)
    }

    val password: EditText by lazy {
        findViewById(R.id.password)
    }
    val sign_in: Button by lazy {
        findViewById<Button>(R.id.sign_in)
    }
    val sign_up: Button by lazy {
        findViewById<Button>(R.id.sign_up)
    }


    // firebase Authentication기능에 대한 변수
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // firebase Authentication기능을 가져옴
        auth = Firebase.auth

        initLoginButton()
        initSignUpButton()
        initEmailAndPAsswordEditText()

    }

    // 입력값 없음에 대한 예외처리
    private fun initEmailAndPAsswordEditText() {

        userEmail.addTextChangedListener {
            val enable = userEmail.text.isNotEmpty() && password.text.isNotEmpty()
            sign_in.isEnabled = enable
            //sign_up.isEnabled = enable
        }
        password.addTextChangedListener {
            val enable = userEmail.text.isNotEmpty() && password.text.isNotEmpty()
            sign_in.isEnabled = enable
            //sign_up.isEnabled = enable
        }

    }


    // 로그인 구현
    private fun initLoginButton() {
        sign_in.setOnClickListener {

            val email = getInputEmail()
            val password = getInputPassword()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(
                            Intent(this, MainActivity::class.java)
                        )

                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    // 회원가입 구현
    private fun initSignUpButton() {
        sign_up.setOnClickListener {

//            val email = getInputEmail()
//            val password = getInputPassword()
//
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(this, "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show()
//                        doLogin(email, password)
//
//                    } else {
//                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
            // SignUpActivity로 이동하는 코드
            startActivity(Intent(this, SignUpActivity::class.java))

        }
    }


    private fun doLogin(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { // it: Task<AuthResult!>
                if (it.isSuccessful) {
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getInputEmail() = userEmail.text.toString()
    private fun getInputPassword() = password.text.toString()
}




