package com.example.swe401swe2304437

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etNameOrEmail = findViewById<EditText>(R.id.etLoginName)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLoginSubmit)
        val txtSignupLink = findViewById<TextView>(R.id.txtGoToSignup)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        val dbHelper = DatabaseHelper(this)
        val btnQuitApp: Button = findViewById(R.id.btnQuitApp)

        btnQuitApp.setOnClickListener {
            finishAffinity()
        }

        btnLogin.setOnClickListener {
            val loginInput = etNameOrEmail.text.toString().trim()
            val passwordInput = etPassword.text.toString().trim()

            if (loginInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginResult = dbHelper.checkUserLogin(loginInput, passwordInput)

            if (loginResult != null) {
                val parts = loginResult.split(":")
                val loggedInUser = parts[0]
                val userRole = parts[1]

                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("LOGGED_IN_USER", loggedInUser)
                editor.putString("USER_ROLE", userRole)
                editor.apply()

                Toast.makeText(this, "Welcome back, $loggedInUser!", Toast.LENGTH_SHORT).show()

                if (userRole.equals("Admin", ignoreCase = true)) {
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                finish()
            } else {
                Toast.makeText(this, "Invalid username/email or password.", Toast.LENGTH_SHORT).show()
            }
        }

        txtSignupLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}