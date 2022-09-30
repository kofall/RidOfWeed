package com.ridofweed.Activities

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ridofweed.Fragments.PlantDetailFragment
import com.ridofweed.Fragments.ScanFragment
import com.ridofweed.Models.Post
import com.ridofweed.Models.User
import com.ridofweed.R
import kotlinx.android.synthetic.main.fragment_plant_detail.*
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import java.lang.Exception


class DetailActivity : AppCompatActivity() {
    companion object {
        const val SAVED_BITMAP: String = "BITMAP"
        const val SAVED_URI: String = "URI"
        const val SAVED_PLANTNAME: String = "PLANTNAME"
        val fields: List<String> = listOf("username", "email", "password", "rememberMe")
    }

    lateinit var pickedBitmap: Bitmap
    var pickedUri: Uri? = null
    lateinit var plantName: String
    var frag: PlantDetailFragment? = null

    lateinit var fb: FloatingActionButton
    lateinit var popupClose: ImageButton
    lateinit var dialogAddPost: Dialog
    lateinit var popupUsername: TextView
    lateinit var popupImageView: ImageView
    lateinit var popupTitle: EditText
    lateinit var popupDescription: EditText
    lateinit var popupShareButton: Button
    lateinit var name: TextView

    lateinit var fbStorage: StorageReference
    lateinit var fbDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initView(savedInstanceState)
        initFragment()
        initListeners()
    }

    private fun initView(savedInstanceState: Bundle?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if(savedInstanceState == null) {
            pickedBitmap = intent.getParcelableExtra(ScanFragment.EXTRA_BITMAP)!!
            intent.getStringExtra(ScanFragment.EXTRA_URI)?.let {
                pickedUri = Uri.parse(it)
            }
            plantName = intent.getStringExtra(ScanFragment.EXTRA_RESULT).toString()
        } else {
            plantName = savedInstanceState.getString(SAVED_PLANTNAME).toString()
            savedInstanceState.getString(SAVED_URI)?.let {
                pickedUri = Uri.parse(it)
            }
            pickedBitmap = savedInstanceState.getParcelable(SAVED_BITMAP)!!
        }

        val web = Web(plantName)
        web.execute()

        if (pickedUri == null) {
            pickedUri = getImageUri(this, pickedBitmap)
        }

        fb = findViewById(R.id.fab_newPost)
        initPopup()

        fbStorage = FirebaseStorage.getInstance().reference.child("blog_images")
    }

    private fun initPopup() {
        val user = User(getRecord())

        dialogAddPost = Dialog(this)
        dialogAddPost.setContentView(R.layout.popup_new_post)
        dialogAddPost.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddPost.window?.setLayout(android.widget.Toolbar.LayoutParams.MATCH_PARENT, android.widget.Toolbar.LayoutParams.WRAP_CONTENT)
        dialogAddPost.window?.attributes?.gravity = Gravity.TOP

        popupUsername = dialogAddPost.findViewById(R.id.newPostUsername)
        popupImageView = dialogAddPost.findViewById(R.id.newPostImageView)
        popupTitle = dialogAddPost.findViewById(R.id.newPostTitle)
        popupDescription = dialogAddPost.findViewById(R.id.newPostDescription)
        popupShareButton = dialogAddPost.findViewById(R.id.btn_shareNewPost)
        popupClose = dialogAddPost.findViewById(R.id.btn_closePopup)

        popupUsername.text = user.username
        popupImageView.setImageBitmap(pickedBitmap)
    }

    private fun initFragment() {
        frag = supportFragmentManager.findFragmentById(R.id.detail_fragment) as PlantDetailFragment?
        frag?.setUp(plantName, pickedBitmap, pickedUri!!)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver,
            inImage,
            "Title",
            null)
        return Uri.parse(path)
    }

    private fun initListeners() {
        fb.setOnClickListener {
            dialogAddPost.show()
        }
        popupClose.setOnClickListener {
            dialogAddPost.hide()
        }
        popupShareButton.setOnClickListener {
            if (popupTitle.text.isEmpty() || popupDescription.text.isEmpty()) {
                makeToast("All fields must be filled")
            } else {
                pickedUri?.let {
                    val imageFilePath = fbStorage.child(it.lastPathSegment.toString())
                    imageFilePath.putFile(it).addOnSuccessListener {
                        imageFilePath.downloadUrl.addOnSuccessListener { uri ->
                            val imageDownloadLink = uri.toString()
                            val post = Post(
                                popupUsername.text.toString(),
                                popupTitle.text.toString(),
                                popupDescription.text.toString(),
                                imageDownloadLink
                            )
                            addPost(post)
                        }.addOnFailureListener { e ->
                            e.message?.let { msg -> makeToast(msg) }
                        }
                    }.addOnFailureListener { e ->
                        e.message?.let { msg -> makeToast(msg) }
                    }
                }
            }
        }
    }

    private fun addPost(post: Post) {
        fbDatabase = FirebaseDatabase.getInstance().getReference("posts").push()
        val key = fbDatabase.key
        post.postKey = key.toString()
        fbDatabase.setValue(post).addOnSuccessListener {
            makeToast("Post added successfully.")
            dialogAddPost.dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_PLANTNAME, plantName)
        outState.putParcelable(SAVED_BITMAP, pickedBitmap)
        outState.putParcelable(SAVED_URI, pickedUri)
    }

    private fun makeToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private class Web(var toSearch: String): AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            var sourceUrl = ""
            try {
                val query = toSearch.replace(" ", "+")
                val googleUrl = "https://www.google.com/search?tbm=isch&q=$query"
                val doc = Jsoup.connect(googleUrl).get()
                val media = doc.select("img").first()
//                sourceUrl = media?.attr("src").toString()
                sourceUrl = media?.absUrl("src").toString()
            } catch (e: Exception) {
                e.message?.let { Log.e("ERROR", it) }
            }
            return null
        }
    }

    fun getRecord(): List<String> {
        val shared = applicationContext.getSharedPreferences("RidOfWeedShared",0)
        val values = ArrayList<String>()
        for (field in fields) {
            shared?.let {
                values.add(it.getString(field, "null").toString())
            }
        }
        return values
    }
}