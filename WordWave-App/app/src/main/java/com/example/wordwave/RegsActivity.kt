package com.example.wordwave

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regs)

        dbHelper = DatabaseHelper(this)

        val nameEditText = findViewById<EditText>(R.id.sgu_name)
        val emailEditText = findViewById<EditText>(R.id.sgu_email)
        val passwordEditText = findViewById<EditText>(R.id.sgu_password)
        val confirmPasswordEditText = findViewById<EditText>(R.id.sgu_repassword)
        val signUpButton = findViewById<Button>(R.id.sgu_button)
        val signInLink = findViewById<TextView>(R.id.sgu_signin_link)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.emailExists(email)) {
                Toast.makeText(this, "This email is already registered. Please sign in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newRowId = dbHelper.addUser(name, email, password)

            if (newRowId != -1L) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                // ✅ Save user session so they stay logged in
                val sharedPref = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
                sharedPref.edit().apply {
                    putBoolean("IS_LOGGED_IN", true)
                    putString("USER_NAME", name)
                    putString("USER_EMAIL", email)
                    apply()
                }

                // ✅ Send user directly to HomeActivity
                val intent = Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("USER_NAME", name)
                    putExtra("USER_EMAIL", email)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        signInLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}