package com.ridofweed.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ridofweed.Fragments.StartAnimationFragment
import com.ridofweed.Models.Plant
import com.ridofweed.R
import kotlinx.coroutines.*

class StartActivity : AppCompatActivity() {
    private lateinit var fragment: StartAnimationFragment

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        fragment.startAnimation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        initView()

        val thread = Thread() {
            val intent = Intent(this, AccountActivity::class.java)
            runBlocking {
                val tasks = listOf(
//                    async(Dispatchers.IO) { loadData() },
                    async(Dispatchers.IO) { delay(6000) }
                )
                tasks.awaitAll()
                startActivity(intent)
                finish()
            }
        }
        thread.start()
    }

//    lateinit var fbDatabase: DatabaseReference
//    fun loadData() {
//        for(plant: Plant in Plant.plantsStaticData) {
//            fbDatabase = FirebaseDatabase.getInstance().getReference("plants").push()
//            val key = fbDatabase.key
//            plant.plantKey = key.toString()
//            fbDatabase.setValue(plant)
//        }
//    }

    fun initView() {
        loadFragment()
    }

    fun loadFragment() {
        fragment = StartAnimationFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.init_fragment_container, fragment)
        ft.commit()
    }
}