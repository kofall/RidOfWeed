package com.ridofweed.Fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ridofweed.Activities.MainActivity
import com.ridofweed.Adapters.PostAdapter
import com.ridofweed.Models.Post
import com.ridofweed.Models.User
import com.ridofweed.R

class LoginFragment : Fragment() {
    companion object {
        val fields: List<String> = listOf("username", "email", "password", "rememberMe")
    }

    lateinit var email: TextView
    lateinit var password: TextView
    lateinit var rememberMe: CheckBox
    lateinit var login: Button
    lateinit var register: TextView

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
        initView()
        initListeners()
    }

    private fun initView() {
        val values = getRecord()
        if (values[3] != "null") {
            if (values[3] == "true") {
                email.text = values[1]
                password.text = values[2]
            } else {
                rememberMe.isChecked = false
            }
        }
    }

    private fun initListeners() {
        login.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                makeToast("All fields must be filled")
            } else {
                signIn(email, password)
            }
        }

        register.setOnClickListener {
            changeFragment.changeFragment(2)
        }
    }

    private fun signIn(email: String, password: String) {
        fbAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fbDatabase = FirebaseDatabase.getInstance().getReference("users")
                    fbDatabase.addValueEventListener(
                        object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (usersnap: DataSnapshot in snapshot.children) {
                                    val user = usersnap.getValue(User::class.java) as User
                                    if (user.email == email) {
                                        setRecord(listOf(
                                            user.username,
                                            user.email,
                                            user.password,
                                            rememberMe.isChecked.toString()))
                                        break
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        }
                    )
                    makeToast("You have successfully logged in")
                    logged()
                } else {
                    makeToast("Logging in failed")
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
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        email = view.findViewById(R.id.loginEmail)
        password = view.findViewById(R.id.loginPassword)
        rememberMe = view.findViewById(R.id.rememberMe)
        login = view.findViewById(R.id.btn_login)
        register = view.findViewById(R.id.tvbtn_signUp)

        fbAuth = FirebaseAuth.getInstance()

        return view
    }

    fun setRecord(values: List<String>) {
        val shared = this.context?.getSharedPreferences("RidOfWeedShared",0)
        val edit = shared?.edit()
        for (i in fields.indices) {
            edit?.putString(RegisterFragment.fields[i], values[i])
        }
        edit?.apply()
    }

    fun getRecord(): List<String> {
        val shared = this.context?.getSharedPreferences("RidOfWeedShared",0)
        val values = ArrayList<String>()
        for (field in fields) {
            shared?.let {
                values.add(it.getString(field, "null").toString())
            }
        }
        return values
    }

    private fun makeToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}