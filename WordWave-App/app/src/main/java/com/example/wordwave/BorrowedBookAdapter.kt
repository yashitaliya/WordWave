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
import java.text.SimpleDateFormat
import java.util.Locale

class BorrowedBookAdapter(
    private val context: Context,
    private val borrowedBookList: List<BorrowedBook>
) : RecyclerView.Adapter<BorrowedBookAdapter.BorrowedBookViewHolder>() {

    inner class BorrowedBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage: ImageView = itemView.findViewById(R.id.book_cover)
        val bookTitle: TextView = itemView.findViewById(R.id.book_title)
        val bookAuthor: TextView = itemView.findViewById(R.id.book_author)
        val borrowInfo: TextView = itemView.findViewById(R.id.borrow_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowedBookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        return BorrowedBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BorrowedBookViewHolder, position: Int) {
        val book = borrowedBookList[position]

        // This part is for displaying the return date in the My Books list
        val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val returnDate = try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(book.returnDate)
            displayFormat.format(date!!)
        } catch (e: Exception) {
            book.returnDate.split(" ")[0]
        }

        holder.bookTitle.text = book.bookName
        holder.bookAuthor.text = book.author
        holder.borrowInfo.text = "Return Date: $returnDate"
        holder.borrowInfo.visibility = View.VISIBLE

        Glide.with(context)
            .load(book.coverUri)
            .placeholder(R.drawable.ic_placeholder_book)
            .into(holder.bookImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfViewerActivity::class.java).apply {
                putExtra("PDF_ASSET_PATH", book.pdfUri)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = borrowedBookList.size
}