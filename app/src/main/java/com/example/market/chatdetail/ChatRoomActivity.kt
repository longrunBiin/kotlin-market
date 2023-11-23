package com.example.market.chatdetail

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.market.DBKey.Companion.DB_CHATS
import com.example.market.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val chatList = mutableListOf<ChatItem>()
    private val adapter = ChatItemAdapter()
    private var chatDB: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)


        val userId = auth.currentUser?.uid.orEmpty()
        val chatKey = intent.getStringExtra("chatKey")

        //chatDB = Firebase.database.reference.child(DB_CHATS).child(chatkey.orEmpty())
        chatDB = Firebase.database.reference.child(DB_CHATS).child(userId).child(chatKey.toString())

        chatDB?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem ?: return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        findViewById<RecyclerView>(R.id.chatRecyclerView).adapter = adapter
        findViewById<RecyclerView>(R.id.chatRecyclerView).layoutManager = LinearLayoutManager(this)

        // 현재시간 넣기
        val t_date = Date(System.currentTimeMillis())
        val nowTime = SimpleDateFormat("yyyy-MM-dd kk:mm", Locale("ko", "KR"))
            .format(t_date)

        findViewById<Button>(R.id.sendButton).setOnClickListener {
            val userId = auth.currentUser?.uid // 현재 로그인한 사용자의 uid를 가져옵니다.
            if (userId != null) {
                // Firebase Realtime Database에서 UserInfo를 가져옵니다.
                val userRef = Firebase.database.reference.child("UserInfo").child(userId)
                userRef.get().addOnSuccessListener {
                    // 이메일 정보를 가져옵니다.
                    val email = it.child("email").value.toString()

                    val chatItem = ChatItem(
                        senderId = email,
                        message = findViewById<EditText>(R.id.messageEditText).text.toString(),
                        time = nowTime
                    )

                    chatDB?.push()?.setValue(chatItem)
                    adapter.notifyDataSetChanged()

                }
            }
        }
    }
}