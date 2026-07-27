package com.example.swe401swe2304437

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etResetEmail = findViewById<EditText>(R.id.etResetEmail)
        val btnVerifyEmail = findViewById<Button>(R.id.btnVerifyEmail)
        val dbHelper = DatabaseHelper(this)

        btnVerifyEmail.setOnClickListener {
            val emailInput = etResetEmail.text.toString().trim()

            if (emailInput.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emailExists = dbHelper.doesEmailExist(emailInput)

            if (emailExists) {
                Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, ResetPasswordActivity::class.java).apply {
                    putExtra("VERIFIED_EMAIL", emailInput)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error: Email address not registered.", Toast.LENGTH_LONG).show()
            }
        }
    }
}