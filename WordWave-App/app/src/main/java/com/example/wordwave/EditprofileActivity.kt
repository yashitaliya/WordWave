package com.example.wordwave

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.util.Calendar

class EditprofileActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var emailTxt: EditText
    private lateinit var usernameTxt: EditText
    private lateinit var dobTxt: EditText
    private lateinit var mobNoTxt: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var saveBtn: AppCompatButton

    private var currentUserEmail: String = "" // Store logged-in user's email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        db = DatabaseHelper(this)
        val back_arrow = findViewById<ImageView>(R.id.back_arrow)
        back_arrow.setOnClickListener {
            finish()
        }

        // Initialize views
        emailTxt = findViewById(R.id.emailTxt)
        usernameTxt = findViewById(R.id.usernameTxt)
        dobTxt = findViewById(R.id.dobTxt)
        mobNoTxt = findViewById(R.id.mobNoTxt)
        radioGroup = findViewById(R.id.radioGroup)
        saveBtn = findViewById(R.id.save_btn)

        // Disable email editing because it is the primary key and cannot be changed easily
        emailTxt.isEnabled = false

        // Get current user email from intent
        currentUserEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        // Load existing user data
        loadUserData()

        // Set up date picker for DOB
        dobTxt.setOnClickListener {
            showDatePicker()
        }

        // Save button click
        saveBtn.setOnClickListener {
            if (validateInputs()) {
                updateProfile()
            }
        }
    }

    private fun loadUserData() {
        if (currentUserEmail.isEmpty()) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val user = db.getUserByEmail(currentUserEmail)
        user?.let {
            emailTxt.setText(it.email)
            usernameTxt.setText(it.name)
            // Use '?' to safely handle null values for optional fields
            dobTxt.setText(it.dob ?: "")
            mobNoTxt.setText(it.mobileno ?: "")
        } ?: run {
            Toast.makeText(this, "User data could not be loaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                dobTxt.setText(date)
            },
            year, month, day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun validateInputs(): Boolean {
        val username = usernameTxt.text.toString().trim()
        val mobile = mobNoTxt.text.toString().trim()
        val dob = dobTxt.text.toString().trim()

        // Username validation
        if (username.isEmpty()) {
            usernameTxt.error = "Username is required"
            usernameTxt.requestFocus()
            return false
        }

        if (username.length < 3) {
            usernameTxt.error = "Username must be at least 3 characters"
            usernameTxt.requestFocus()
            return false
        }

        // Mobile validation (optional)
        if (mobile.isNotEmpty()) {
            if (mobile.length != 10) {
                mobNoTxt.error = "Enter a valid 10-digit mobile number"
                mobNoTxt.requestFocus()
                return false
            }
            if (!mobile.matches(Regex("^[0-9]+$"))) {
                mobNoTxt.error = "Mobile number can only contain digits"
                mobNoTxt.requestFocus()
                return false
            }
        }

        // DOB validation (optional)
        if (dob.isEmpty()) {
            // It's optional, so we can let it pass if empty
        } else if (!dob.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
            dobTxt.error = "Enter a valid date format (DD/MM/YYYY)"
            dobTxt.requestFocus()
            return false
        }

        // Gender validation
        if (radioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun updateProfile() {
        val newUsername = usernameTxt.text.toString().trim()
        val newDob = dobTxt.text.toString().trim()
        val newMobile = mobNoTxt.text.toString().trim()

        // Get selected gender
        val selectedGenderId = radioGroup.checkedRadioButtonId
        val gender = findViewById<RadioButton>(selectedGenderId).text.toString()

        // IMPORTANT: The updateUser method in your DatabaseHelper.kt expects name, dob, and mobile
        // It does NOT accept gender, since you haven't added a gender column to your database yet.
        // We will call the updateUser function correctly now.

        val result = db.updateUser(
            email = currentUserEmail,
            newName = newUsername,
            newDob = if (newDob.isEmpty()) null else newDob,
            newMobileno = if (newMobile.isEmpty()) null else newMobile
        )

        if (result > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }
}