package com.example.acropolisassignment.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.example.acropolisassignment.R
import com.example.acropolisassignment.bookBrowsing.BookBrowsingActivity
import com.example.acropolisassignment.db.entity.User
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private lateinit var context: Context
    private var userViewModel: UserViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        toolbar = findViewById(R.id.toolbar_singUp)
        etFirstName = findViewById(R.id.et_signUp_firstName)
        etLastName = findViewById(R.id.et_signUp_lastName)
        etEmail = findViewById(R.id.et_signUp_email)
        etPassword = findViewById(R.id.et_signUp_password)
        etConfirmPassword = findViewById(R.id.et_signUp_confirmPassword)

        context = this

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewModel()
    }

    private fun setupViewModel() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        userViewModel?.getSignUpLiveData()?.observe(this, Observer<Long> {row ->
            if (row != -1L) {
                val intent = Intent()
                intent.putExtra("user_id", row.toInt())
                setResult(BookBrowsingActivity.USER_SCREEN_CODE, intent)
                finish()
            } else {
                Toast.makeText(context, "Failed to Sign Up", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun signUp(view: View) {
        if (isEmptyFieldValidationPassed()) {
            if (isValidEmailAddress(etEmail.text.toString())) {
                if (passwordMatchValidation()) {
                    userViewModel?.singUp(User(
                        firstName = etFirstName.text!!.toString(),
                        lastName = etLastName.text!!.toString(),
                        email = etEmail.text!!.toString(),
                        password = etPassword.text.toString()
                    ))
                } else {
                    Toast.makeText(context, "Password does not match", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Provide valid Email Address", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show()
        }
    }

    private fun isEmptyFieldValidationPassed(): Boolean {
        return etFirstName.text!!.isNotEmpty()
                && etLastName.text!!.isNotEmpty()
                && etEmail.text!!.isNotEmpty()
                && etPassword.text!!.isNotEmpty()
                && etConfirmPassword.text!!.isNotEmpty()
    }

    private fun isValidEmailAddress(email: String?): Boolean {
        val ePattern =
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }

    private fun passwordMatchValidation(): Boolean {
        return etPassword.text.toString() == etConfirmPassword.text.toString()
    }
}