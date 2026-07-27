package com.example.swe401swe2304437

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmNewPassword = findViewById<EditText>(R.id.etConfirmNewPassword)
        val btnSubmitNewPassword = findViewById<Button>(R.id.btnSubmitNewPassword)
        val dbHelper = DatabaseHelper(this)

        val targetEmail = intent.getStringExtra("VERIFIED_EMAIL")

        if (targetEmail == null) {
            Toast.makeText(this, "Session error. Please restart the process.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnSubmitNewPassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmNewPassword.text.toString().trim()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.length < 5) {
                Toast.makeText(this, "Password must be at least 5 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isUpdated = dbHelper.updatePassword(targetEmail, newPassword)

            if (isUpdated) {
                Toast.makeText(this, "Password changed successfully! Please login.", Toast.LENGTH_LONG).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Database update operation failed. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}