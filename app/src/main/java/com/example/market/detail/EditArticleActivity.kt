package com.example.market.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.market.DBKey
import com.example.market.R
import com.example.market.home.ArticleModel
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore

class EditArticleActivity : AppCompatActivity() {

    private lateinit var editTitleEditText: EditText
    private lateinit var editPriceEditText: EditText
    private lateinit var editDescriptionEditText: EditText
    private lateinit var editSubmitButton: Button
    private lateinit var editProgressBar: ProgressBar

    private lateinit var chatKey: String
    private lateinit var title: String
    private lateinit var price: String
    private lateinit var description: String
    private lateinit var imageUrl: String

    private val articleDB: DatabaseReference by lazy {
        com.google.firebase.ktx.Firebase.database.reference.child(DBKey.DB_ARTICLES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_article)

        editTitleEditText = findViewById(R.id.editTitleEditText)
        editPriceEditText = findViewById(R.id.editPriceEditText)
        editDescriptionEditText = findViewById(R.id.editDescriptionEditText)
        editSubmitButton = findViewById(R.id.editSubmitButton)
        editProgressBar = findViewById(R.id.editProgressBar)

        // 인텐트에서 전달된 데이터 가져오기
        chatKey = intent.getStringExtra("chatKey") ?: ""
        title = intent.getStringExtra("title") ?: ""
        price = intent.getStringExtra("price") ?: ""
        description = intent.getStringExtra("description") ?: ""
        imageUrl = intent.getStringExtra("imageUrl") ?: ""

        val priceWithoutWon = price.replace("원", "")

        // 기존 데이터를 EditText에 설정
        editTitleEditText.setText(title)
        editPriceEditText.setText(priceWithoutWon)
        editDescriptionEditText.setText(description)

        editSubmitButton.setOnClickListener {
            Log.d("EditArticleActivity", "editSubmitButton clicked")
            val updatedTitle = editTitleEditText.text.toString()
            val updatedPrice = editPriceEditText.text.toString()
            val updatedDescription = editDescriptionEditText.text.toString()

            Log.d("EditArticleActivity", "Updated values: title=$updatedTitle, price=$updatedPrice, description=$updatedDescription")



            val resultIntent = Intent().apply {
                putExtra("title", updatedTitle)
                putExtra("price", updatedPrice)
                putExtra("description", updatedDescription)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            updateArticle()
            finish()
        }
    }

    private fun updateArticle() {
        val updatedTitle = editTitleEditText.text.toString().trim()
        val updatedPrice = editPriceEditText.text.toString().trim()
        val updatedDescription = editDescriptionEditText.text.toString().trim()

        if (chatKey.isNotEmpty()) {
            Log.d("EditArticleActivity", "Attempting to update article with chatKey: $chatKey")

            // 기존 데이터 읽기
            articleDB.child(chatKey).get().addOnSuccessListener { snapshot ->
                val article = snapshot.getValue(ArticleModel::class.java) ?: return@addOnSuccessListener

                // 필드 변경
                article.title = updatedTitle
                article.price = updatedPrice
                article.description = updatedDescription

                Log.d("EditArticleActivity", "Article fields updated: $article")

                // 데이터 쓰기
                snapshot.ref.setValue(article).addOnSuccessListener {
                    Log.d("EditArticleActivity", "Update successful. Updated values: $article")

                    // 결과 설정 및 액티비티 종료
                    val intent = Intent().apply {
                        putExtra("title", updatedTitle)
                        putExtra("price", updatedPrice)
                        putExtra("description", updatedDescription)
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()

                }.addOnFailureListener { e ->
                    Log.e("EditArticleActivity", "Update failed: ${e.message}")
                }
            }.addOnFailureListener { e ->
                Log.e("EditArticleActivity", "Failed to read the article: ${e.message}")
            }
        } else {
            Log.e("EditArticleActivity", "chatKey is empty")
        }
    }

}
