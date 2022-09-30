package com.ridofweed.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ridofweed.Activities.MainActivity
import com.ridofweed.Models.User
import com.ridofweed.R
import java.lang.Exception

class RegisterFragment : Fragment() {
    companion object {
        val fields: List<String> = listOf("username", "email", "password", "rememberMe")
    }

    lateinit var username: TextView
    lateinit var email: TextView
    lateinit var password: TextView
    lateinit var signup: Button
    lateinit var signin: TextView

    lateinit var fbAuth: FirebaseAuth
    lateinit var fbDatabase: DatabaseReference

    interface ChangeFragment {
        fun changeFragment(changeOn: Int)
    }
    private lateinit var changeFragment: ChangeFragment

    interface Finish {
        fun finishMe()
    }
    private lateinit var finishListener: Finish

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.changeFragment = context as ChangeFragment
        this.finishListener = context as Finish
    }

    override fun onStart() {
        super.onStart()
        initListeners()
    }

    private fun initListeners() {
        signup.setOnClickListener {
            val username = username.text.toString()
            val email = email.text.toString()
            val password = password.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                makeToast("All fields must be filled")
            } else {
                tryCreateUserAccount(username, email, password)
            }
        }

        signin.setOnClickListener {
            changeFragment.changeFragment(1)
        }
    }

    private fun tryCreateUserAccount(username: String, email: String, password: String) {
        fbDatabase.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.children.count() == 0) {
                        createUserAccount(username, email, password)
                    } else {
                        for (data in snapshot.children) {
                            if (!data.child(username).exists()) {
                                createUserAccount(username, email, password)
                            } else {
                                makeToast("Account of a given name already exists.")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
        )
    }

    private fun createUserAccount(username: String, email: String, password: String) {
        fbAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(username, email, password)
                    fbDatabase.child("users").child(username).setValue(user)
                    setRecord(listOf(username, email, password, "true"))
                    makeToast("Account created.")
                    logged()
                } else {
                    makeToast("Account creation failed - " + task.exception!!.message)
                }
            }
    }

    private fun logged() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        finishListener.finishMe()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        username = view.findViewById(R.id.registerUsername)
        email = view.findViewById(R.id.registerEmail)
        password = view.findViewById(R.id.registerPassword)
        signup = view.findViewById(R.id.btn_signUp)
        signin = view.findViewById(R.id.tvbtn_signIn)

        fbAuth = FirebaseAuth.getInstance()
        fbDatabase = FirebaseDatabase.getInstance().reference

        return view
    }

    fun setRecord(values: List<String>){
        val shared = this.context?.getSharedPreferences("RidOfWeedShared",0)
        val edit = shared?.edit()
        for (i in fields.indices) {
            edit?.putString(fields[i], values[i])
        }
        edit?.apply()
    }

    private fun makeToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}