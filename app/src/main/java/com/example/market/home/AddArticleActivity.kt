package com.example.market.home

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.view.isVisible
import com.example.market.DBKey.Companion.DB_ARTICLES
import com.example.market.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddArticleActivity : AppCompatActivity() {

    private var selectedUri: Uri? = null

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }
    private val description: String by lazy {
        findViewById<EditText>(R.id.detailDescriptionTextView).text.toString().orEmpty()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_article)

        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES) -> {
                    showPermissionContextPopup()
                }

                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                        1010
                    )
                }

            }
        }

        // push().setValue() 를 통해 해당 DB위치에 ArticleModel을 통으로 올려서 저장
        // 이렇게 Data Model을 통으로 push()하여 넣으면, Realtime Database 상에는
        // 헤당 DB 위치에 임의의 식별용 Key를 만들고 그 안에 해당 DataModel의 데이터를 넣게 된다.
        // ( 즉, 데이터를 새롭게 넣을 때마다 해당 위치에 임의의 Key가 만들어지고, 그 Key의 Value로서 Data Model이 통으로 들어가게 된다. )
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString().orEmpty()
            val price = findViewById<EditText>(R.id.priceEditText).text.toString().orEmpty()
            val sellerId = auth.currentUser?.uid.orEmpty()
            val status = Status.ONSALE

            // 해당 작업은 test 결과 경우에 따라 2초 정도 요소되는 등 상당히 긴 시간이 소요되었다..
            // 따라서 사용자가 진행이 정상적으로 되었고 있다고 인식할 수 있도록 하기 위해 ProgressBar를 병행해서 사용해줘야 한다.
            // 만약 ProgressBar 도중에 사용자의 Touch로 인한 TouchEvent를 막고 싶다면,
            // View를 하나 더 만들어서 그 최상단의 View가 ProgressBar가 돌고 있는 중에 나머지의 Touch를 막는 코드를 작성하면 된다.
            showProgress()

            // todo 중간에 이미지가 있으면 업로드 과정을 추가
            if (selectedUri != null) {
                // 매우 희박한 확률로 다른 쓰래드에 의해 해당 변수가 null처리 될 수도 있으므로 예외처리
                val PhotoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(
                    PhotoUri,
                    successHandler = { uri ->
                        uploadArticle(sellerId, title, price, uri, description)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()

                        hideProgress()
                        // ProgressBar 감추기
                    }
                )
            } else {
                uploadArticle(sellerId, title, price, "", description)

            }

        }

    }

    // firebase Storage에 데이터 넣기 + 데이터 가져오기 를 구현한 메소드 생성
    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        // 이미지 파일의 이름에 대한 형식설정( 이름이 겹치면 안되므로 현재 시간을 이름으로 함 )
        val fileName = "${System.currentTimeMillis()}.png"

        // 데이터 넣는 부분 --> DB의 최상위 항목에 접근하는 부분까지는 RealtimeDB와 동일함
        // 하지만, DB를 탐색하는 부분은 파일탐색기와 비슷한 방식을 사용 -> 예를들어, child("article/photo")이라면 article폴더 안의 photo폴더 안으로 이동
        // 이후 child(파일 이름)를 통해 해당 영역에 파일을 만들어 줄 수 있음 ( 적합한 확장자를 포함시킬 것 )
        // addOnCompliteListener를 통해 데이터가 올바르게 들어갔는지 확인 및 그에 따른 처리를 해줄 수 있음
        storage.reference.child("article/photo").child(fileName)
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // 데이터가 잘 들어갔다면

                    // 데이터를 가져오는 부분
                    // DB에서 원하는 이미지 항목에 접근, downloadUrl에 해당 항목의 Value가 들어있으므로 그것을 가져옴
                    // 이후  addOnSuccessListener와 addOnFailureListener를 통해 데이터를 성공적으로 가져온 경우와 그렇지 못한 경우에 대한 처리
                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }
                        .addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    // 위의 코드에서 이 메소드가 사용된 부분을 보면 if로 image를 다루는 넣는 영역은 비동기이고 else로 빠진 영역은 동기이므로,
    // 메소드를 사용하여 각각의 영역에서 데이터를 넣는 식으로 코딩해주어야 한다.
    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl: String, description: String) {
        // push()를 먼저 호출하고 chatKey를 얻습니다.
        val newArticleReference = articleDB.push()
        val chatKey = newArticleReference.key ?: throw Exception("Could not get chatKey.")

        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), price, imageUrl, "ONSALE", description, chatKey)
        newArticleReference.setValue(model)

        hideProgress()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)
    }

    private fun showProgress(){
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true

    }
    private fun hideProgress(){
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1010
                )
            }
            .create()
            .show()

    }
}