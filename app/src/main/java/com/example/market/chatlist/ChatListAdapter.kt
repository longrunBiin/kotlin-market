package com.example.market.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.databinding.ItemArticleBinding
import com.example.market.databinding.ItemChatListBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(val onItemClicked: (ChatListItem) -> Unit) : ListAdapter<ChatListItem, ChatListAdapter.ViewHolder>(diffUtil) {

    // ViewBinding을 통해 레이아웃에서 가져옴
    inner class ViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatListItem: ChatListItem) {

            binding.root.setOnClickListener{
                onItemClicked(chatListItem)
            }

            binding.chatRoomTitleTextView.text = chatListItem.itemTitle

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatListItem>() {

            // 현재 노출되고 있는 아이템과 새로운 아이템이 같은지 확인 ㅡ, 새로운 아이템이 들어오면 호출됨
            // 일반적으로 키값을 통해 구분하게 됨
            override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem.key == newItem.key
            }

            // 현재 아이템과 새로운 아이탬의 = 여부를 확인
            override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}