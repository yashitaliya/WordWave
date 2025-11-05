package com.example.wordwave

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyBooksActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var borrowedRecyclerView: RecyclerView
    private lateinit var noBooksTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_books)

        dbHelper = DatabaseHelper(this)

        val backArrow: ImageView = findViewById(R.id.back_arrow)
        backArrow.setOnClickListener { finish() }

        borrowedRecyclerView = findViewById(R.id.borrowed_books_recycler)
        noBooksTextView = findViewById(R.id.no_books_text)

        val userEmail = intent.getStringExtra("USER_EMAIL")

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "User not identified. Please re-login.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadBorrowedBooks(userEmail)
    }

    private fun loadBorrowedBooks(userEmail: String) {
        val borrowedBooks = dbHelper.getBorrowedBooksByUser(userEmail)

        if (borrowedBooks.isEmpty()) {
            noBooksTextView.visibility = View.VISIBLE
            borrowedRecyclerView.visibility = View.GONE
        } else {
            noBooksTextView.visibility = View.GONE
            borrowedRecyclerView.visibility = View.VISIBLE

            val numberOfColumns = 2
            borrowedRecyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
            borrowedRecyclerView.adapter = BorrowedBookAdapter(this, borrowedBooks)
        }
    }
}