package com.ridofweed.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.ridofweed.Activities.DetailActivity
import com.ridofweed.Adapters.PostAdapter
import com.ridofweed.Models.Plant
import com.ridofweed.Models.Post
import com.ridofweed.Models.User
import com.ridofweed.R
import kotlinx.android.synthetic.main.fragment_plant_detail.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class PlantDetailFragment : Fragment() {
    companion object {
        const val SAVED_BITMAP: String = "BITMAP"
        const val SAVED_URI: String = "URI"
        const val SAVED_PLANTNAME: String = "PLANTNAME"
    }

    lateinit var plantName: String
    lateinit var pickedBitmap: Bitmap
    lateinit var pickedUri: Uri

    lateinit var name: TextView
    lateinit var latin: TextView
    lateinit var is_weed: TextView
    lateinit var soil_type: TextView
    lateinit var season: TextView
    lateinit var growth: TextView
    lateinit var height: TextView
    lateinit var preferred_temperature: TextView
    lateinit var preferred_humidity: TextView
    lateinit var easy_to_remove: TextView
    lateinit var plant_image: ImageView
    lateinit var fbDatabase: DatabaseReference

    fun setUp(name: String, bitmap: Bitmap, uri: Uri) {
        this.plantName = name
        this.pickedBitmap = bitmap
        this.pickedUri = uri
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun initView() {
        view?.let {
            plant_image.setImageBitmap(pickedBitmap)
            fbDatabase = FirebaseDatabase.getInstance().getReference("plants")
            fbDatabase.addValueEventListener(
                object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (plantsnap: DataSnapshot in snapshot.children) {
                            val plant = plantsnap.getValue(Plant::class.java) as Plant
                            if (plant.latinPlantName == plantName) {
                                plant_name.text = plant.plantName
                                latin.text = plant.latinPlantName
                                is_weed.text = plant.isWeed
                                soil_type.text = plant.soilType
                                season.text = plant.season
                                growth.text = plant.growth
                                height.text = plant.height
                                preferred_temperature.text = plant.preferredTemperature
                                preferred_humidity.text = plant.preferredHumidity
                                easy_to_remove.text = plant.easyToRemove
                                return
                            }
                        }
                        name.text = plantName
                        latin.text = "That is only a demo version."
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                }
            )

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            plantName = savedInstanceState.getString(SAVED_PLANTNAME).toString()
            savedInstanceState.getString(SAVED_URI)?.let {
                pickedUri = Uri.parse(it)
            }
            pickedBitmap = savedInstanceState.getParcelable(SAVED_BITMAP)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_detail, container, false)
        name = view.findViewById(R.id.plant_name)
        latin = view.findViewById(R.id.latin)
        is_weed = view.findViewById(R.id.is_weed)
        soil_type = view.findViewById(R.id.soil_type)
        season = view.findViewById(R.id.season)
        growth = view.findViewById(R.id.growth)
        height = view.findViewById(R.id.height)
        preferred_temperature = view.findViewById(R.id.preferred_temperature)
        preferred_humidity = view.findViewById(R.id.preferred_humidity)
        easy_to_remove = view.findViewById(R.id.easy_to_remove)
        plant_image = view.findViewById(R.id.plant_image)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_PLANTNAME, plantName)
    }
}