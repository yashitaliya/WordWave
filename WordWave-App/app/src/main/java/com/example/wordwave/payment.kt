package com.example.wordwave
// payment.kt

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.io.Serializable // Needed for retrieving a Serializable object

class payment : AppCompatActivity() {

    private var selectedDurationId: Int = -1
    private lateinit var dbHelper: DatabaseHelper
    private var userEmail: String? = null // Stored user email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        dbHelper = DatabaseHelper(this)
        userEmail = intent.getStringExtra("USER_EMAIL")

        val back_arrow = findViewById<ImageView>(R.id.back_arrow)
        back_arrow.setOnClickListener {
            finish()
        }

        val duration_1 = findViewById<AppCompatButton>(R.id.duration_1)
        val duration_2 = findViewById<AppCompatButton>(R.id.duration_2)
        val duration_3 = findViewById<AppCompatButton>(R.id.duration_3)
        val cardLayout = findViewById<LinearLayout>(R.id.card_linear)
        val upiId = findViewById<EditText>(R.id.upi_id)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val borrowBtn = findViewById<AppCompatButton>(R.id.borrow_btn)

        val bookCoverImageView: ImageView = findViewById(R.id.imageView2)
        val bookNameTextView: TextView = findViewById(R.id.textView2)
        val authorNameTextView: TextView = findViewById(R.id.textView3)

        val book = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("BOOK_OBJECT", Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("BOOK_OBJECT") as? Book
        }

        if (book != null && !userEmail.isNullOrEmpty()) {
            bookNameTextView.text = book.bookName
            authorNameTextView.text = book.author
            Glide.with(this)
                .load(book.coverUri)
                .placeholder(R.drawable.ic_placeholder_book)
                .into(bookCoverImageView)

            borrowBtn.setOnClickListener {
                if (validateInputs()) {
                    val durationDays = when (selectedDurationId) {
                        R.id.duration_1 -> 7
                        R.id.duration_2 -> 14
                        R.id.duration_3 -> 30
                        else -> 0
                    }

                    if (durationDays > 0) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val borrowDate = dateFormat.format(Date())

                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_YEAR, durationDays)
                        val returnDate = dateFormat.format(calendar.time)

                        dbHelper.addBorrowedBook(
                            userEmail!!,
                            book.id,
                            borrowDate,
                            returnDate
                        )

                        Toast.makeText(this, "Payment successful! Book borrowed until ${returnDate.split(" ")[0]}", Toast.LENGTH_LONG).show()

                        // Launch the new PdfViewerActivity
                        val pdfViewerIntent = Intent(this, PdfViewerActivity::class.java).apply {
                            putExtra("PDF_ASSET_PATH", book.pdfUri)
                        }
                        startActivity(pdfViewerIntent)

                        finish()
                    } else {
                        Toast.makeText(this, "Internal error: Invalid duration selected.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Book or User details not provided.", Toast.LENGTH_SHORT).show()
            finish()
        }

        val buttons = listOf(duration_1, duration_2, duration_3)
        buttons.forEach { button ->
            button.setOnClickListener {
                buttons.forEach {
                    it.setBackgroundResource(R.drawable.rounded_button_bg)
                    it.setTextColor(Color.BLACK)
                }
                button.setBackgroundResource(R.drawable.btn_bg)
                button.setTextColor(Color.WHITE)
                selectedDurationId = button.id
            }
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_card -> {
                    cardLayout.visibility = View.VISIBLE
                    upiId.visibility = View.GONE
                }
                R.id.radio_upi -> {
                    cardLayout.visibility = View.GONE
                    upiId.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (selectedDurationId == -1) {
            Toast.makeText(this, "Please select a rental duration.", Toast.LENGTH_SHORT).show()
            return false
        }
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val checkedId = radioGroup.checkedRadioButtonId
        if (checkedId == -1) {
            Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show()
            return false
        }
        when (checkedId) {
            R.id.radio_card -> {
                val cardName = findViewById<EditText>(R.id.card_name).text.toString()
                val cardNumber = findViewById<EditText>(R.id.card_num).text.toString()
                val cvvNumber = findViewById<EditText>(R.id.cvv_num).text.toString()
                if (cardName.isEmpty()) {
                    Toast.makeText(this, "Please enter card holder name.", Toast.LENGTH_SHORT).show()
                    return false
                }
                if (cardNumber.length != 16) {
                    Toast.makeText(this, "Card number must be 16 digits long.", Toast.LENGTH_SHORT).show()
                    return false
                }
                if (cvvNumber.length != 3) {
                    Toast.makeText(this, "CVV must be 3 digits long.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            R.id.radio_upi -> {
                val upiId = findViewById<EditText>(R.id.upi_id).text.toString()
                if (upiId.isEmpty() || !upiId.contains("@")) {
                    Toast.makeText(this, "Please enter a valid UPI ID.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        return true
    }
}