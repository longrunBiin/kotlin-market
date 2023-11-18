package com.example.market.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.databinding.ItemArticleBinding
import com.example.market.detail.DetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(val onItemClicked: (ArticleModel) -> Unit) : ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // ViewBinding을 통해 레이아웃에서 가져옴
    inner class ViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(articleModel: ArticleModel) {
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(articleModel.createdAt)

            binding.titleTextView.text = articleModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.priceTextView.text = "${articleModel.price}원"
            binding.statusTextView.text = articleModel.status

//            if (articleModel.imageUrl.isNotEmpty()) {
//                Glide.with(binding.thumbnailImageView)
//                    .load(articleModel.imageUrl)
//                    .into(binding.thumbnailImageView)
//            }
//
//            binding.root.setOnClickListener{
//                onItemClicked(articleModel)
//            }
            if (articleModel.imageUrl.startsWith("gs://")) {
                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(articleModel.imageUrl)

                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Glide.with(binding.thumbnailImageView)
                        .load(imageUrl)
                        .into(binding.thumbnailImageView)
                }.addOnFailureListener {
                    // URL을 가져오는 데 실패한 경우
                }
            } else {
                // Firebase Storage 경로가 아닌 경우는 이미지 URL로 간주
                Glide.with(binding.thumbnailImageView)
                    .load(articleModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val articleModel = currentList[position]
        holder.bind(articleModel)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java).apply {
                putExtra("chatKey", articleModel.chatKey)
                putExtra("title", articleModel.title)
                putExtra("price", articleModel.price)
                putExtra("description", articleModel.description)
                putExtra("imageUrl", articleModel.imageUrl)
                putExtra("isSeller", articleModel.sellerId == auth.currentUser?.uid)

            }
            holder.itemView.context.startActivity(intent)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {

            // 현재 노출되고 있는 아이템과 새로운 아이템이 같은지 확인 ㅡ, 새로운 아이템이 들어오면 호출됨
            // 일반적으로 키값을 통해 구분하게 됨
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            // 현재 아이템과 새로운 아이탬의 = 여부를 확인
            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }
        }
    }

}