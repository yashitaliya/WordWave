package com.example.wordwave

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class BookActivity : AppCompatActivity() {

    private var bookId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        val back_arrow: ImageView = findViewById(R.id.back_arrow)
        back_arrow.setOnClickListener {
            finish()
        }

        val dbHelper = DatabaseHelper(this)

        bookId = intent.getIntExtra("BOOK_ID", -1)

        if (bookId == -1) {
            Toast.makeText(this, "Book ID not provided.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val book = dbHelper.getBookById(bookId)
        if (book == null) {
            Toast.makeText(this, "Book not found in database.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val bookImageView: ImageView = findViewById(R.id.bookimg)
        val bookNameTextView: TextView = findViewById(R.id.bookname)
        val authorNameTextView: TextView = findViewById(R.id.authorname)
        val descriptionTextView: TextView = findViewById(R.id.description)
        val priceTextView: TextView = findViewById(R.id.price)
        val borrowButton: Button = findViewById(R.id.borrowbtn)

        bookNameTextView.text = book.bookName
        authorNameTextView.text = book.author
        descriptionTextView.text = book.description
        priceTextView.text = "₹${book.price}"

        Glide.with(this)
            .load(book.coverUri)
            .placeholder(R.drawable.ic_placeholder_book)
            .into(bookImageView)

        borrowButton.setOnClickListener {
            val userEmail = intent.getStringExtra("USER_EMAIL")

            val intent = Intent(this, payment::class.java).apply {
                putExtra("BOOK_ID", bookId)
                putExtra("USER_EMAIL", userEmail)
                putExtra("BOOK_OBJECT", book)
            }
            startActivity(intent)
        }
    }
}