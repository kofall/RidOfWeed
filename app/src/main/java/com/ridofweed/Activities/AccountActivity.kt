package com.ridofweed.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.ridofweed.Fragments.LoginFragment
import com.ridofweed.R
import com.ridofweed.Fragments.RegisterFragment

class AccountActivity :
    AppCompatActivity(),
    LoginFragment.ChangeFragment,
    LoginFragment.Finish,
    RegisterFragment.ChangeFragment,
    RegisterFragment.Finish {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        initView()
    }

    private fun initView() {
        val loginFragment = LoginFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.account_fragment_container, loginFragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun changeFragment(changeOn: Int) {
        val ft = supportFragmentManager.beginTransaction()
        if(changeOn == 1) {
            val loginFragment = LoginFragment()
            ft.replace(R.id.account_fragment_container, loginFragment)
        } else if (changeOn == 2) {
            val registerFragment = RegisterFragment()
            ft.replace(R.id.account_fragment_container, registerFragment)
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun finishMe() {
        finish()
    }
}