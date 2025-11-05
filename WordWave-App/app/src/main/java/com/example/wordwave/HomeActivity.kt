package com.example.wordwave

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var featuredRecyclerView: RecyclerView
    private lateinit var menuButton: ImageButton
    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var bookAdapter: BookAdapter
    private var allBooks: List<Book> = listOf()
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        dbHelper = DatabaseHelper(this)
        initializeViews()
        setupNavigation()
        setupRecyclerView()
        setupSearch()
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        menuButton = findViewById(R.id.btn_menu)
        featuredRecyclerView = findViewById(R.id.featured_books_recycler)
        searchInput = findViewById(R.id.search_input)
        searchButton = findViewById(R.id.btn_search_inside)
    }

    private fun setupNavigation() {
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = navigationView.getHeaderView(0)
        userEmail = intent.getStringExtra("USER_EMAIL") ?: "user.name@example.com"
        val userName = intent.getStringExtra("USER_NAME") ?: "User Name"

        val userNameText = headerView.findViewById<TextView>(R.id.userName)
        val userEmailText = headerView.findViewById<TextView>(R.id.userEmail)

        userNameText.text = userName
        userEmailText.text = userEmail

        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupRecyclerView() {
        allBooks = dbHelper.getAllBooks()
        bookAdapter = BookAdapter(this, allBooks, userEmail)
        featuredRecyclerView.layoutManager = GridLayoutManager(this, 2)
        featuredRecyclerView.adapter = bookAdapter
    }

    private fun setupSearch() {
        searchButton.setOnClickListener {
            performSearch(searchInput.text.toString())
        }

        searchInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                performSearch(s.toString())
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredBooks = if (query.isEmpty()) {
            allBooks
        } else {
            allBooks.filter { book ->
                book.bookName.contains(query, ignoreCase = true) ||
                        book.author.contains(query, ignoreCase = true) ||
                        book.description.contains(query, ignoreCase = true)
            }
        }
        bookAdapter = BookAdapter(this, filteredBooks, userEmail)
        featuredRecyclerView.adapter = bookAdapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_profile -> {
                val profileIntent = Intent(this, ProfileActivity::class.java)
                profileIntent.putExtra("USER_EMAIL", userEmail)
                startActivity(profileIntent)
            }
            R.id.my_books -> {
                val myBooksIntent = Intent(this, MyBooksActivity::class.java)
                myBooksIntent.putExtra("USER_EMAIL", userEmail)
                startActivity(myBooksIntent)
            }
            R.id.about_us -> {
                startActivity(Intent(this, AboutUs::class.java))
            }
            R.id.faq -> {
                startActivity(Intent(this, faq::class.java))
            }
            R.id.logout -> {
                val sharedPref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    clear()
                    apply()
                }

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
