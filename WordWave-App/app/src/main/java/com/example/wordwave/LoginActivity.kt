package com.example.wordwave

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Check if user is already logged in
        val sharedPref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("IS_LOGGED_IN", false)

        if (isLoggedIn) {
            val savedName = sharedPref.getString("USER_NAME", "User Name")
            val savedEmail = sharedPref.getString("USER_EMAIL", "user.name@example.com")

            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("USER_NAME", savedName)
                putExtra("USER_EMAIL", savedEmail)
            }
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        val emailEditText = findViewById<EditText>(R.id.lgn_email)
        val passwordEditText = findViewById<EditText>(R.id.lgn_password)
        val signInButton = findViewById<Button>(R.id.lgn_btn)
        val signUpLink = findViewById<TextView>(R.id.lgn_signup_link)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if user exists
            if (dbHelper.checkUser(email, password)) {
                val user = dbHelper.getUserByEmail(email)

                if (user != null) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                    // ✅ Save login state
                    with(sharedPref.edit()) {
                        putBoolean("IS_LOGGED_IN", true)
                        putString("USER_NAME", user.name)
                        putString("USER_EMAIL", user.email)
                        apply()
                    }

                    val intent = Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("USER_NAME", user.name)
                        putExtra("USER_EMAIL", user.email)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error retrieving user details.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_SHORT).show()
            }
        }

        signUpLink.setOnClickListener {
            startActivity(Intent(this, RegsActivity::class.java))
        }
    }
}