package com.example.market

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

import com.example.market.chatlist.ChatListFragment
import com.example.market.home.HomeFragment
import com.example.market.mypage.MyPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

// androidx에 있는 Fragment를 import 해와야 함


class MainActivity : AppCompatActivity() {

    private val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // fragment 초기값

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            requestSinglePermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        else
            requestSinglePermission(Manifest.permission.READ_MEDIA_IMAGES)

        replaceFragment(myPageFragment)
        // setOnNavigationItemSelectedListener는 네비게이션바의 탭들이 선택되었을 때 호출되어 선택된 탭의 id가 내려온다.
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList -> replaceFragment(chatListFragment)
                R.id.myPage -> replaceFragment(myPageFragment)

            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    // FrameLayout에 선택된 Fragment를 attach하는 메소드
    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }

    private fun requestSinglePermission(permission: String) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            return

        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it == false) { // permission is not granted!
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")
                    setMessage("permission required!")
                }.show()
            }
        }

        if (shouldShowRequestPermissionRationale(permission)) {
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage("permission required!")
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(permission) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(permission)
        }
    }
}