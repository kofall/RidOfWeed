package com.ridofweed.Fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.ridofweed.Activities.DetailActivity
import com.ridofweed.R
import com.ridofweed.ml.PlantModel
import org.tensorflow.lite.support.image.TensorImage

class ScanFragment : Fragment() {
    companion object {
        const val REQUEST_LOAD_PICTURE = 100
        const val EXTRA_BITMAP: String = "BITMAP"
        const val EXTRA_URI: String = "URI"
        const val EXTRA_RESULT: String = "RESULT"
    }

    private lateinit var btnCapture: Button
    private lateinit var ibCapture: ImageButton
    private lateinit var btnLoad: Button
    private lateinit var ibLoad: ImageButton
    var pickedBitmap: Bitmap? = null
    var pickedUri: Uri? = null
    var plantName: String? = null

    override fun onStart() {
        super.onStart()

        initView()
        initListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    private fun initView() {
        view?.let {
            btnCapture = it.findViewById(R.id.btn_capturePicture)
            ibCapture = it.findViewById(R.id.ib_capturePicture)
            btnLoad = it.findViewById(R.id.btn_loadPicture)
            ibLoad = it.findViewById(R.id.ib_loadPicture)
        }
    }

    private fun initListeners() {
        btnCapture.setOnClickListener {
            takePicture()
        }
        ibCapture.setOnClickListener {
            takePicture()
        }
        btnLoad.setOnClickListener {
            loadPicture()
        }
        ibLoad.setOnClickListener {
            loadPicture()
        }
    }

    private fun showDetails() {
        plantName = outputGenerator(pickedBitmap!!)

        if (plantName.equals("None")) {
            buildAlert("Nothing found", "Could not recognise any plant from the picture.")
            pickedBitmap = null
            pickedUri = null
        } else {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_BITMAP, pickedBitmap)
            intent.putExtra(EXTRA_URI, pickedUri.toString())
            intent.putExtra(EXTRA_RESULT, plantName)
            activity?.startActivity(intent)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun buildAlert(title: String, message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { _: DialogInterface, _: Int -> }
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        dialog.show()
    }

    // ** TAKE PICTURE FROM CAMERA ** //

    private fun takePicture() {
        if (view?.let {
                ContextCompat.checkSelfPermission(it.context,
                    android.Manifest.permission.CAMERA)
            } != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionTakePicture.launch(android.Manifest.permission.CAMERA)
        } else {
            takePicturePreview.launch(null)
        }
    }

    private val requestPermissionTakePicture = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if(granted) {
            takePicture()
        } else {
            view?.let {
                Toast.makeText(it.context, "Permission denied.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if(bitmap != null) {
            pickedBitmap = bitmap
            pickedUri = null
            showDetails()
        }
    }

    // ** LOAD PICTURE FROM DISK ** //

    private fun loadPicture() {
        if (view?.let {
                ContextCompat.checkSelfPermission(it.context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLoadPicture.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            val mimetypes = arrayOf("image/jpeg", "image/png", "image/jpg")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            pickPicture.launch(intent)
        }
    }

    private val requestPermissionLoadPicture = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if(granted) {
            loadPicture()
        } else {
            view?.let {
                Toast.makeText(it.context, "Permission denied.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val pickPicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result != null)
            onResultReceived(REQUEST_LOAD_PICTURE, result)
    }

    private fun onResultReceived(requestCode: Int, result: ActivityResult?) {
        if (requestCode == REQUEST_LOAD_PICTURE && result?.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                pickedUri = uri
                view?.let {
                    pickedBitmap = MediaStore.Images.Media.getBitmap(it.context.contentResolver, uri)
                }
                pickedBitmap = Bitmap.createScaledBitmap(pickedBitmap!!, 240, 240, false)
                showDetails()
            }
        }
    }

    // ** PREDICT OUTPUT ** //

    private fun outputGenerator(bitmap: Bitmap): String {
        val model = view?.let { PlantModel.newInstance(it.context) } as PlantModel
        // Creates inputs for reference.
        val image = TensorImage.fromBitmap(bitmap)
        // Runs model inference and gets result.
        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList.apply {
            sortByDescending { it.score }
        }
        // Releases model resources if no longer used.
        model.close()

        return probability[0].label
    }
}