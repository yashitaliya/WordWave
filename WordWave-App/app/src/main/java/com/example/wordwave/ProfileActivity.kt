package com.example.wordwave

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var userEmail: String = ""

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var dobTextView: TextView
    private lateinit var mobileTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = DatabaseHelper(this)

        val back_arrow = findViewById<ImageView>(R.id.back_arrow)
        back_arrow.setOnClickListener {
            finish()
        }

        // Get the email passed from login
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "No user email provided", Toast.LENGTH_SHORT).show()
            finish() // Close activity if no email is provided
            return
        }

        // Initialize all views
        nameTextView = findViewById(R.id.usernameTxt)
        emailTextView = findViewById(R.id.emailTxt)
        dobTextView = findViewById(R.id.dobTxt) // Assumes you have a TextView with this ID
        mobileTextView = findViewById(R.id.mobNoTxt) // Assumes you have a TextView with this ID

        val editProfileBtn: Button = findViewById(R.id.editBtn)

        // Load and display user data
        loadUserData()

        // Edit Profile button click
        editProfileBtn.setOnClickListener {
            val intent = Intent(this, EditprofileActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
    }

    private fun loadUserData() {
        if (userEmail.isNotEmpty()) {
            val user = db.getUserByEmail(userEmail)
            user?.let {
                nameTextView.text = it.name
                emailTextView.text = it.email
                // Correctly set the text for DOB and Mobile, handling nulls
                dobTextView.text = it.dob ?: "----------"
                mobileTextView.text = it.mobileno ?: "----------"
            } ?: run {
                // Handle case when user is null
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload data from the database to reflect changes from EditprofileActivity
        loadUserData()
    }
}