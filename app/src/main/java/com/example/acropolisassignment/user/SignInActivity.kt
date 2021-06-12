package com.example.acropolisassignment.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.acropolisassignment.R
import com.example.acropolisassignment.bookBrowsing.BookBrowsingActivity
import com.example.acropolisassignment.db.entity.User
import com.google.android.material.textfield.TextInputEditText

class SignInActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var etEmail: TextInputEditText
    lateinit var etPassword: TextInputEditText

    lateinit var context: Context
    private var mUserViewModel: UserViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        toolbar = findViewById(R.id.toolbar_singIn)
        etEmail = findViewById(R.id.et_signIn_email)
        etPassword = findViewById(R.id.et_signIn_password)
        context = this
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewModel()
    }

    private fun setupViewModel() {
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        mUserViewModel?.getSignInLiveData()?.observe(this,
            Observer { user ->
                if (user != null) {
                    val intent = Intent()
                    intent.putExtra("user_id", user.uid)
                    setResult(BookBrowsingActivity.USER_SCREEN_CODE, intent)
                    finish()
                } else {
                    Toast.makeText(context, "Sign In failed", Toast.LENGTH_LONG).show()
                }
            })
    }

    fun signIn(view: View) {
        if (isValidationPassed()) {
            mUserViewModel?.signIn(etEmail.text!!.toString(), etPassword.text!!.toString())
        } else {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show()
        }
    }

    private fun isValidationPassed(): Boolean {
        return etEmail.text!!.isNotEmpty()
                && etPassword.text!!.isNotEmpty()
    }
}