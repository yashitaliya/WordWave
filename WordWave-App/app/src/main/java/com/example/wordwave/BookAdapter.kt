package com.example.wordwave

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BookAdapter(
    private val context: Context,
    private val bookList: List<Book>,
    private val userEmail: String?
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage: ImageView = itemView.findViewById(R.id.book_cover)
        val bookTitle: TextView = itemView.findViewById(R.id.book_title)
        val bookAuthor: TextView = itemView.findViewById(R.id.book_author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]

        holder.bookTitle.text = book.bookName
        holder.bookAuthor.text = book.author
        Glide.with(context)
            .load(book.coverUri)
            .placeholder(R.drawable.ic_placeholder_book)
            .into(holder.bookImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookActivity::class.java).apply {
                putExtra("BOOK_ID", book.id)
                putExtra("USER_EMAIL", userEmail)
            }
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = bookList.size
}