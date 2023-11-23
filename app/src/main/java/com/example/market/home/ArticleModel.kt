package com.example.market.home

// Firebase RealTime Database에서 그대로 넣고 뺴기 위해 정의한 Data Model
// 이후ㅡ, RealTime Database에서는 해당 객체의 형식으로 통채로 넣고, 해당 객체 형식의 데이터를 통채로 받아올 것이다.
data class ArticleModel(
    val sellerId: String,
    var title: String,
    val createdAt: Long,
    var price: String,
    val imageUrl: String,
    //val userName: String,
    var status: String = Status.ONSALE.name,
    var description: String,
    val chatKey: String? = null
){
    constructor(): this("","",0,"","", "", "")
}
// Firebase RealTime Database에서 Model Class를 통해 데이터를 주고 받고 싶을 떄는
// 반드시 위와 같이 빈 생성자를 정의해줘야 한다. -> ( 아마 null 예외처리 때문인 듯 함 )