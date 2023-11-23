package com.example.market.chatlist

data class ChatListItem(
    val buyerId: String,
    val sellerId : String,
    val itemTitle: String,
    val key: String
){

    constructor(): this("","","","")
}