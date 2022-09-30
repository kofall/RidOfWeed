package com.ridofweed.Fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.AppComponentFactory
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.ridofweed.R
import org.json.JSONObject
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.math.roundToInt


class HomeFragment : Fragment() {
    lateinit var city: TextView
    lateinit var temperature: TextView
    lateinit var humidity: TextView
    lateinit var wind_speed: TextView
    lateinit var cloudiness: TextView
    lateinit var pressure: TextView
    lateinit var country: String
    private var url: String = "https://api.openweathermap.org/data/2.5/weather"
    private var appid = "bfdab6078d7338536846d9a6f2ca36dc"

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                initView(location)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat
                .checkSelfPermission(requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat
                .checkSelfPermission(requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun initView(location: Location?) {
        city.text = "Unknown localization"
        if (location != null) {
            try {
                val geocoder = Geocoder(context)
                val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                city.text = address[0].locality
                country = address[0].countryName
                getWeatherDetails()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        city = view.findViewById(R.id.city)
        temperature = view.findViewById(R.id.temperature)
        humidity = view.findViewById(R.id.humidity)
        wind_speed = view.findViewById(R.id.wind_speed)
        cloudiness = view.findViewById(R.id.cloudiness)
        pressure = view.findViewById(R.id.pressure)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())

        locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 500
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        return view
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                onStart()
            } else {
                view?.let {
                    Toast.makeText(it.context, "Permission denied.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    @SuppressLint("SetTextI18n")
    private fun getWeatherDetails() {
        if (city.text != "─── ───") {
            val tempUrl = url + "?q=" + city.text + "&appid=" + appid;
            val stringRequest = StringRequest(Request.Method.POST, tempUrl, { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val jsonObjectMain = jsonResponse.getJSONObject("main")
                    temperature.text = (jsonObjectMain.getDouble("temp") - 273.15).roundToInt().toString() + " °C"
                    humidity.text = jsonObjectMain.getInt("humidity").toString() + " %"
                    pressure.text = jsonObjectMain.getInt("pressure").toString() + " hPa"
                    val jsonObjectWind = jsonResponse.getJSONObject("wind")
                    wind_speed.text = jsonObjectWind.getString("speed") + " m/s"
                    val jsonObjectClouds = jsonResponse.getJSONObject("clouds")
                    cloudiness.text = jsonObjectClouds.getString("all") + " %"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Log.e("ERROR", error.toString().trim())
            })
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }
}