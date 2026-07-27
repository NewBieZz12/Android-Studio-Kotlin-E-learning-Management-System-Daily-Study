package com.example.swe401swe2304437

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etName = findViewById<EditText>(R.id.etSignupName)
        val etEmail = findViewById<EditText>(R.id.etSignupEmail)
        val etPassword = findViewById<EditText>(R.id.etSignupPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignupSubmit)
        val txtLoginLink = findViewById<TextView>(R.id.txtGoToLogin)

        val dbHelper = DatabaseHelper(this)

        btnSignup.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            if (!email.endsWith("@gmail.com", ignoreCase = true) || email.length <= 10) {
                Toast.makeText(this, "Email must be a valid @gmail.com address", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if (password.length < 5) {
                Toast.makeText(this, "Password must be at least 5 characters long", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val isSuccess = dbHelper.registerUser(name, email, password, "User")

            if (isSuccess) {
                Toast.makeText(this, "Account created successfully! Please Log In.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Registration failed. Email might already exist.", Toast.LENGTH_SHORT).show()
            }
        }

        txtLoginLink.setOnClickListener {
            finish()
        }
    }
}