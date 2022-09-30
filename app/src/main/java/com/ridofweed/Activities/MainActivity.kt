package com.ridofweed.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ridofweed.Fragments.HomeFragment
import com.ridofweed.R

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PAGE: String = "PAGE"
    }
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView(savedInstanceState)
    }

    private fun initView(savedInstanceState: Bundle?) {
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val controller = findNavController(R.id.main_fragment)
        bottomNavigationView.setupWithNavController(controller)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_PAGE, bottomNavigationView.selectedItemId)
    }
}