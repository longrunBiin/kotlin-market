package com.example.market.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.market.DBKey.Companion.CHILD_CHAT
import com.example.market.DBKey.Companion.DB_USERS
import com.example.market.R
import com.example.market.chatdetail.ChatRoomActivity
import com.example.market.databinding.FragmentChatlistBinding
import com.example.market.home.ArticleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListFragment:Fragment(R.layout.fragment_chatlist) {

    private var binding: FragmentChatlistBinding? = null
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatListItem>()

    private lateinit var chatDB: DatabaseReference

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentChatlistBinding = FragmentChatlistBinding.bind(view)
        binding = fragmentChatlistBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { ChatRoom ->
            // todo 채팅방으로 이동하는 코드

            val intent = Intent(requireContext(),ChatRoomActivity::class.java)
            intent.putExtra("chatKey", ChatRoom.key)
            startActivity(intent)

        })

        chatRoomList.clear()

        fragmentChatlistBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatlistBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        if (auth.currentUser == null){
            return
        }

        chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser!!.uid).child(CHILD_CHAT)


        // todo Data model 통채로 DB에 넣었던 것을 통채로 가져오는 부분 ( DB에 넣을 때 Data model 통채로 넣었기에 통채로 가져오는 것이 가능한 것임 )
        // todo SingleValueEvent로 Data model을 불러올 경우에는 해당 리스너가 SingleValue로 데이터를 가져오므로 불러올 영역에 존재하는 Data model들을 담은 하나의 List로 들어오게 된다.
        // todo  따라서 forEach로 들어온 List에서 Data model을 하나씩 분리하여 가져와야 한다.
        chatDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // todo 서버에서 데이터를 가져오는 것에 성공하면 호출


                // snapshot.children에 Data model들을 담은 하나의 리스트가 내려옴
                // 이 리스트에서 Data model들을 하나씩 분리하는 작업이 필요 ( forEach )
                snapshot.children.forEach{
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }

                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO 서버에서 데이터를 가져오는 것에 실패했을 경우 호출
            }
        })

    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.notifyDataSetChanged()
    }
}