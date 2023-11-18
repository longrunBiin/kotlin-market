package com.example.market.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.market.LoginActivity
import com.example.market.R
import com.example.market.databinding.FragmentChatlistBinding
import com.example.market.databinding.FragmentMypageBinding

import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    // 현재 사용자의 UID 가져오기
    val currentUser = Firebase.auth.currentUser
    val uid = currentUser?.uid

    // Realtime Database에서 사용자 정보 가져오기
    val database = FirebaseDatabase.getInstance()
    //val usersRef = database.getReference("UserInfo").child(uid)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding


        val out = view.findViewById<Button>(R.id.logout) // 로그아웃
        out.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish() // 현재 액티비티를 종료
        }
    }
}