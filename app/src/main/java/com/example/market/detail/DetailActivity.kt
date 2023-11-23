package com.example.market.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.market.R
import com.example.market.chatdetail.ChatItem
import com.example.market.chatdetail.ChatRoomActivity
import com.example.market.home.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DetailActivity : AppCompatActivity() {



    private lateinit var detailTitleTextView: TextView
    private lateinit var detailUserNameTextView: TextView
    private lateinit var detailPriceTextView: TextView
    private lateinit var detailDescriptionTextView: TextView
    private lateinit var detailStatusTextView: TextView
    private val REQUEST_CODE_EDIT_ARTICLE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        detailTitleTextView = findViewById(R.id.detailTitleTextView)
        detailUserNameTextView = findViewById(R.id.detailUserNameTextView)
        detailPriceTextView = findViewById(R.id.detailPriceTextView)
        detailDescriptionTextView = findViewById(R.id.detailDescriptionTextView)
        detailStatusTextView = findViewById(R.id.detailStatusTextView)
        val detailImageView: ImageView = findViewById(R.id.detailImageView)
        val detailChatButton: Button = findViewById(R.id.detailChatButton)
        val detailEditButton: Button = findViewById(R.id.detailEditButton)


        /*
        val userId = auth.currentUser?.uid // 현재 로그인한 사용자의 uid를 가져옵니다.
        var username = ""
        if (userId != null) {
            // Firebase Realtime Database에서 UserInfo를 가져옵니다.
            val userRef = Firebase.database.reference.child("UserInfo").child(userId)
            userRef.get().addOnSuccessListener {
                // 이메일 정보를 가져옵니다.
                username = it.child("username").value.toString()}}*/

        val chatKey = intent.getStringExtra("chatKey")
        val sellerId = intent.getStringExtra("sellerId") ?: ""
        val userName = intent.getStringExtra("userName") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val price = intent.getStringExtra("price") ?: ""
        val priceWithWon = "$price"+"원"
        val description = intent.getStringExtra("description") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val status = intent.getStringExtra("status")
        Log.d("DetailActivity", "Received status: $status")

        detailTitleTextView.text = title
        detailUserNameTextView.text = userName
        detailPriceTextView.text = priceWithWon
        detailDescriptionTextView.text = description
        detailStatusTextView.text = if (status == Status.ONSALE.name || status == null) "판매중" else "판매완료"

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.han_mask)
            .error(R.drawable.han_mask)
            .into(detailImageView)

        val isSeller = intent.getBooleanExtra("isSeller", false)
        if (isSeller) {
            detailChatButton.visibility = View.GONE
            detailEditButton.visibility = View.VISIBLE

            detailEditButton.setOnClickListener {
                val intent = Intent(this, EditArticleActivity::class.java)
                intent.putExtra("chatKey", chatKey)
                intent.putExtra("title", title)
                intent.putExtra("price", price)
                intent.putExtra("description", description)
                intent.putExtra("imageUrl", imageUrl)
                val status = if (detailStatusTextView.text == "판매중") Status.ONSALE.name else Status.SOLDOUT.name
                intent.putExtra("status", status)  // 'status' 정보 추가
                startActivityForResult(intent, REQUEST_CODE_EDIT_ARTICLE)
            }

        } else {
            detailChatButton.visibility = View.VISIBLE
            detailEditButton.visibility = View.GONE

            detailChatButton.setOnClickListener {
                // 채팅 버튼 클릭 시 처리
                val chatKey = intent.getStringExtra("chatKey")

                // ChatRoomActivity를 시작하고 필요한 정보를 전달합니다.
                val intent = Intent(this, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", chatKey)
                startActivity(intent)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_ARTICLE && resultCode == Activity.RESULT_OK) {
            val updatedTitle = data?.getStringExtra("title")
            val updatedPrice = data?.getStringExtra("price")
            val updatedDescription = data?.getStringExtra("description")
            val updatedPriceWithWon = "$updatedPrice"+"원"
            val updatedStatus = data?.getStringExtra("status")


            detailTitleTextView.text = updatedTitle
            detailPriceTextView.text = updatedPriceWithWon
            detailDescriptionTextView.text = updatedDescription
            detailStatusTextView.text = if (updatedStatus == Status.ONSALE.name) "판매중" else "판매완료"
            intent.putExtra("status", updatedStatus)
        }
    }
}
