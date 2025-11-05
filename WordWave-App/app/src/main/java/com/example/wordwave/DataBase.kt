package com.example.wordwave

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.Serializable
import java.util.Locale

// New data class for borrowed book tracking
data class BorrowedBook(
    val borrowId: Int,
    val userEmail: String,
    val bookId: Int,
    val bookName: String,
    val author: String,
    val coverUri: String,
    val pdfUri: String,
    val borrowDate: String,
    val returnDate: String
)

// Data class for a user
data class User(
    val name: String,
    val email: String,
    val password: String,
    val dob: String? = null,
    val mobileno: String? = null
)

// Corrected Book data class
data class Book(
    val id: Int,
    val bookName: String,
    val author: String,
    val price: Double,
    val description: String,
    val coverUri: String,
    val pdfUri: String
): Serializable

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "wordwave.db"
        // Database version incremented to 6 to create the new table
        private const val DB_VERSION = 6

        // User table constants
        const val TABLE_USERS = "regsdetails"
        const val COL_NAME = "name"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
        const val COL_DOB = "dob"
        const val COL_MOBILENO = "mobileno"

        // Book table constants
        const val TABLE_BOOKS = "books"
        const val COL_ID = "id"
        const val COL_BOOK_NAME = "book_name"
        const val COL_AUTHOR = "author"
        const val COL_DESCRIPTION = "description"
        const val COL_PRICE = "price"
        const val COL_COVER_URI = "cover_uri"
        const val COL_PDF_URI = "pdf_uri"

        // New Borrowed Books table constants
        const val TABLE_BORROWED_BOOKS = "borrowed_books"
        const val COL_BORROW_ID = "borrow_id"
        const val COL_USER_EMAIL = "user_email"
        const val COL_BORROW_DATE = "borrow_date"
        const val COL_RETURN_DATE = "return_date"
        // COL_ID from books table is reused for the book foreign key

        private const val SQL_CREATE_USERS = """
            CREATE TABLE $TABLE_USERS(
                $COL_NAME TEXT NOT NULL,
                $COL_EMAIL TEXT PRIMARY KEY NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_DOB TEXT,
                $COL_MOBILENO TEXT
            )
        """
        private const val SQL_DROP_USERS = "DROP TABLE IF EXISTS $TABLE_USERS"

        private const val SQL_CREATE_BOOKS = """
            CREATE TABLE $TABLE_BOOKS(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_BOOK_NAME TEXT NOT NULL,
                $COL_AUTHOR TEXT NOT NULL,
                $COL_DESCRIPTION TEXT NOT NULL,
                $COL_PRICE REAL NOT NULL,
                $COL_COVER_URI TEXT NOT NULL,
                $COL_PDF_URI TEXT NOT NULL
            )
        """
        private const val SQL_DROP_BOOKS = "DROP TABLE IF EXISTS $TABLE_BOOKS"

        private const val SQL_CREATE_BORROWED_BOOKS = """
            CREATE TABLE $TABLE_BORROWED_BOOKS(
                $COL_BORROW_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_EMAIL TEXT NOT NULL,
                $COL_ID INTEGER NOT NULL,
                $COL_BORROW_DATE TEXT NOT NULL,
                $COL_RETURN_DATE TEXT NOT NULL,
                FOREIGN KEY($COL_USER_EMAIL) REFERENCES $TABLE_USERS($COL_EMAIL),
                FOREIGN KEY($COL_ID) REFERENCES $TABLE_BOOKS($COL_ID)
            )
        """
        private const val SQL_DROP_BORROWED_BOOKS = "DROP TABLE IF EXISTS $TABLE_BORROWED_BOOKS"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_USERS)
        db.execSQL(SQL_CREATE_BOOKS)
        db.execSQL(SQL_CREATE_BORROWED_BOOKS) // New table creation
        addInitialBooks(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(oldVersion < 6) {
            db.execSQL(SQL_DROP_BORROWED_BOOKS) // Drop new table on upgrade
            db.execSQL(SQL_DROP_USERS)
            db.execSQL(SQL_DROP_BOOKS)
            onCreate(db)
        }
    }

    private fun addInitialBooks(db: SQLiteDatabase) {
        val books = listOf(
            Book(
                id=0,
                bookName = "A Tree Grows in Brooklyn",
                author = "Betty Smith",
                price = 299.0,
                description = "A coming-of-age story about a young girl growing up in early 20th-century Brooklyn.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_treegrowsbrooklyn,
                pdfUri = "books/A Tree Grows In Brooklyn by Betty Smith.pdf"
            ),
            Book(
                id=0,
                bookName = "A Brief History of the Internet",
                author = "Various Authors",
                price = 150.0,
                description = "An overview of how the internet was born and how it evolved into what we use today.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_historyinternet,
                pdfUri = "books/A-Brief-History-of-the-Internet.pdf"
            ),
            Book(
                id=0,
                bookName = "A History of Art for Beginners and Students",
                author = "Clara Erskine Clement",
                price = 220.0,
                description = "An introduction to the study of art history, suitable for learners and students.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_historyart,
                pdfUri = "books/A-History-of-Art-for-Beginners-and-Students.pdf"
            ),
            Book(
                id=0,
                bookName = "A Journey to the Centre of the Earth",
                author = "Jules Verne",
                price = 180.0,
                description = "A classic science fiction adventure beneath the surface of the Earth.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_journeyearth,
                pdfUri = "books/A-Journey-to-the-Centre-of-the-Earth.pdf"
            ),
            Book(
                id=0,
                bookName = "A Maker of History",
                author = "E. Phillips Oppenheim",
                price = 170.0,
                description = "A political spy thriller with suspense and mystery at its core.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_makerhistory,
                pdfUri = "books/A-Maker-of-History.pdf"
            ),
            Book(
                id=0,
                bookName = "Alcatraz",
                author = "Max Brand",
                price = 160.0,
                description = "A western novel about outlaws, justice, and life on the edge.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_alcatraz,
                pdfUri = "books/Alcatraz.pdf"
            ),
            Book(
                id=0,
                bookName = "Allan Quatermain",
                author = "H. Rider Haggard",
                price = 210.0,
                description = "An adventure novel set in Africa, full of exploration and mystery.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_allanquatermain,
                pdfUri = "books/Allan-Quatermain.pdf"
            ),
            Book(
                id=0,
                bookName = "American Political Ideas",
                author = "John Fiske",
                price = 200.0,
                description = "An academic look at the evolution of American political thought.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_americanpoliticalideas,
                pdfUri = "books/American-Political-Ideas-Viewed-from-the-Standpoint-of-Universal-History.pdf"
            ),
            Book(
                id=0,
                bookName = "Animal Farm",
                author = "George Orwell",
                price = 199.0,
                description = "A political satire about power, corruption, and control.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_animalfarm,
                pdfUri = "books/animalfarm.pdf"
            ),
            Book(
                id=0,
                bookName = "Basic Fluid Mechanics",
                author = "Unknown",
                price = 250.0,
                description = "A technical book explaining core principles of fluid mechanics.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_fluidmechanics,
                pdfUri = "books/Basic_Fluid_Mechanics_and_Hydraulic_Machines.pdf"
            ),
            Book(
                id=0,
                bookName = "Chess History and Reminiscences",
                author = "Henry Edward Bird",
                price = 175.0,
                description = "A history of chess from one of the game’s great masters.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_chesshistory,
                pdfUri = "books/Chess-History-and-Reminiscences.pdf"
            ),
            Book(
                id=0,
                bookName = "Collected Works of Poe",
                author = "Edgar Allan Poe",
                price = 280.0,
                description = "A collection of poems and stories by Edgar Allan Poe.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_collectedpoe,
                pdfUri = "books/Collected-Works-of-Poe.pdf"
            ),
            Book(
                id=0,
                bookName = "Exercise Is Murder",
                author = "Various",
                price = 140.0,
                description = "A light mystery novel with humor and suspense.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_exerciseismurder,
                pdfUri = "books/Exercise-Is-Murder.pdf"
            ),
            Book(
                id=0,
                bookName = "General Science",
                author = "Unknown",
                price = 130.0,
                description = "A beginner-friendly introduction to general science concepts.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_generalscience,
                pdfUri = "books/General-Science.pdf"
            ),
            Book(
                id=0,
                bookName = "German Science Reader",
                author = "Unknown",
                price = 180.0,
                description = "A collection of German scientific writings for study and reference.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_germanscience,
                pdfUri = "books/German-Science-Reader.pdf"
            ),
            Book(
                id=0,
                bookName = "Induction",
                author = "Unknown",
                price = 160.0,
                description = "A technical and logical exploration of inductive reasoning.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_induction,
                pdfUri = "books/Induction.pdf"
            ),
            Book(
                id=0,
                bookName = "Living History",
                author = "Unknown",
                price = 190.0,
                description = "A historical account bringing past events to life.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_livinghistory,
                pdfUri = "books/Living-History.pdf"
            ),
            Book(
                id=0,
                bookName = "Popular Mechanics",
                author = "Various",
                price = 150.0,
                description = "DIY projects, innovations, and engineering explained simply.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_popularmechanics,
                pdfUri = "books/popularmechanics.pdf"
            ),
            Book(
                id=0,
                bookName = "Popular Science",
                author = "Various",
                price = 160.0,
                description = "Explaining science in simple terms for general readers.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_popularscience,
                pdfUri = "books/popularscience.pdf"
            ),
            Book(
                id=0,
                bookName = "Science of Trapping",
                author = "E. Kreps",
                price = 120.0,
                description = "An old classic on the art and science of animal trapping.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_sciencetapping,
                pdfUri = "books/Science-of-Trapping.pdf"
            ),
            Book(
                id=0,
                bookName = "Smoky God",
                author = "Willis George Emerson",
                price = 170.0,
                description = "A fantasy novel exploring the hollow earth theory.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_smokygod,
                pdfUri = "books/smokygod.pdf"
            ),
            Book(
                id=0,
                bookName = "The Adventure of the Bruce Partington Plans",
                author = "Arthur Conan Doyle",
                price = 130.0,
                description = "A Sherlock Holmes mystery about espionage and stolen documents.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_brucepartington,
                pdfUri = "books/The-Adventure-of-the-Bruce-Partington-Plans.pdf"
            ),
            Book(
                id=0,
                bookName = "The Art of War",
                author = "Sun Tzu",
                price = 200.0,
                description = "The ancient Chinese military strategy classic.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_artofwar,
                pdfUri = "books/The-Art-of-War.pdf"
            ),
            Book(
                id=0,
                bookName = "The Breaking Point",
                author = "Mary Roberts Rinehart",
                price = 180.0,
                description = "A suspense novel about love, betrayal, and discovery.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_breakingpoint,
                pdfUri = "books/The-Breaking-Point.pdf"
            ),
            Book(
                id=0,
                bookName = "The Science of Being Well",
                author = "Wallace D. Wattles",
                price = 160.0,
                description = "A self-help classic focusing on physical and mental health.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_beingwell,
                pdfUri = "books/The-Science-of-Being-Well.pdf"
            ),
            Book(
                id=0,
                bookName = "The Science of Human Nature",
                author = "William Henry Pyle",
                price = 140.0,
                description = "A psychology book explaining human behavior and development.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_humannature,
                pdfUri = "books/The-Science-of-Human-Nature.pdf"
            ),
            Book(
                id=0,
                bookName = "The Sign of the Four",
                author = "Arthur Conan Doyle",
                price = 170.0,
                description = "A Sherlock Holmes novel about treasure and crime.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_signfour,
                pdfUri = "books/The-Sign-of-the-Four.pdf"
            ),
            Book(
                id=0,
                bookName = "The Thirty-Nine Steps",
                author = "John Buchan",
                price = 200.0,
                description = "A spy thriller filled with suspense and pursuit.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_thirtynine,
                pdfUri = "books/The-Thirty-Nine-Steps.pdf"
            ),
            Book(
                id=0,
                bookName = "The Trials of the Core",
                author = "Michael Thies",
                price = 220.0,
                description = "A fantasy story about challenges, magic, and destiny.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_trialscore,
                pdfUri = "books/The-Trials-of-the-Core.pdf"
            ),
            Book(
                id=0,
                bookName = "Tight Binding",
                author = "Unknown",
                price = 130.0,
                description = "A mystery novel full of suspense and entanglement.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_tightbinding,
                pdfUri = "books/tightbinding.pdf"
            ),
            Book(
                id=0,
                bookName = "White Nights",
                author = "Fyodor Dostoevsky",
                price = 180.0,
                description = "A romantic short story set in St. Petersburg.",
                coverUri = "android.resource://com.example.wordwave/" + R.drawable.cover_page_whitenights,
                pdfUri = "books/whitenights.pdf"
            )
        )

        books.forEach { book ->
            addBook(book, db)
        }
    }

    private fun addBook(book: Book, db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put(COL_BOOK_NAME, book.bookName)
            put(COL_AUTHOR, book.author)
            put(COL_DESCRIPTION, book.description)
            put(COL_PRICE, book.price)
            put(COL_COVER_URI, book.coverUri)
            put(COL_PDF_URI, book.pdfUri)
        }
        db.insert(TABLE_BOOKS, null, values)
    }

    /**
     * Records a new borrowed book entry in the database.
     */
    fun addBorrowedBook(userEmail: String, bookId: Int, borrowDate: String, returnDate: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_EMAIL, userEmail.trim().lowercase(Locale.ROOT))
            put(COL_ID, bookId)
            put(COL_BORROW_DATE, borrowDate)
            put(COL_RETURN_DATE, returnDate)
        }
        return db.insert(TABLE_BORROWED_BOOKS, null, values)
    }

    /**
     * Retrieves all books borrowed by a specific user, joining with the books table for details.
     */
    fun getBorrowedBooksByUser(email: String): List<BorrowedBook> {
        val books = mutableListOf<BorrowedBook>()
        val db = readableDatabase
        val emailNorm = email.trim().lowercase(Locale.ROOT)

        val query = """
            SELECT 
                bb.$COL_BORROW_ID, 
                bb.$COL_USER_EMAIL, 
                b.$COL_ID, 
                b.$COL_BOOK_NAME, 
                b.$COL_AUTHOR, 
                b.$COL_COVER_URI, 
                b.$COL_PDF_URI,
                bb.$COL_BORROW_DATE,
                bb.$COL_RETURN_DATE
            FROM $TABLE_BORROWED_BOOKS bb
            INNER JOIN $TABLE_BOOKS b ON bb.$COL_ID = b.$COL_ID
            WHERE bb.$COL_USER_EMAIL = ?
        """

        val cursor = db.rawQuery(query, arrayOf(emailNorm))

        cursor.use { c ->
            if (c.moveToFirst()) {
                do {
                    val borrowedBook = BorrowedBook(
                        borrowId = c.getInt(c.getColumnIndexOrThrow(COL_BORROW_ID)),
                        userEmail = c.getString(c.getColumnIndexOrThrow(COL_USER_EMAIL)),
                        bookId = c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                        bookName = c.getString(c.getColumnIndexOrThrow(COL_BOOK_NAME)),
                        author = c.getString(c.getColumnIndexOrThrow(COL_AUTHOR)),
                        coverUri = c.getString(c.getColumnIndexOrThrow(COL_COVER_URI)),
                        pdfUri = c.getString(c.getColumnIndexOrThrow(COL_PDF_URI)),
                        borrowDate = c.getString(c.getColumnIndexOrThrow(COL_BORROW_DATE)),
                        returnDate = c.getString(c.getColumnIndexOrThrow(COL_RETURN_DATE))
                    )
                    books.add(borrowedBook)
                } while (c.moveToNext())
            }
        }
        return books
    }

    // --- User-related methods (Original code is here) ---
    fun addUser(
        name: String,
        email: String,
        passwordPlain: String,
        dob: String? = null,
        mobileno: String? = null
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name.trim())
            put(COL_EMAIL, email.trim().lowercase(Locale.ROOT))
            put(COL_PASSWORD, passwordPlain)
            put(COL_DOB, dob)
            put(COL_MOBILENO, mobileno)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun updateUser(
        email: String,
        newName: String,
        newDob: String? = null,
        newMobileno: String? = null
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, newName.trim())
            put(COL_DOB, newDob)
            put(COL_MOBILENO, newMobileno)
        }
        return db.update(
            TABLE_USERS,
            values,
            "$COL_EMAIL = ?",
            arrayOf(email.trim().lowercase(Locale.ROOT))
        )
    }

    fun emailExists(email: String): Boolean {
        val db = readableDatabase
        db.query(
            TABLE_USERS,
            arrayOf(COL_EMAIL),
            "$COL_EMAIL = ?",
            arrayOf(email.trim().lowercase(Locale.ROOT)),
            null, null, null
        ).use { c ->
            return c.moveToFirst()
        }
    }

    fun checkUser(email: String, passwordPlain: String): Boolean {
        val db = readableDatabase
        val emailNorm = email.trim().lowercase(Locale.ROOT)
        db.query(
            TABLE_USERS,
            arrayOf(COL_EMAIL),
            "$COL_EMAIL = ? AND $COL_PASSWORD = ?",
            arrayOf(emailNorm, passwordPlain),
            null, null, null
        ).use { c ->
            return c.moveToFirst()
        }
    }

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        db.query(
            TABLE_USERS,
            arrayOf(COL_NAME, COL_EMAIL, COL_PASSWORD, COL_DOB, COL_MOBILENO),
            "$COL_EMAIL = ?",
            arrayOf(email.trim().lowercase(Locale.ROOT)),
            null, null, null
        ).use { c ->
            return if (c.moveToFirst()) {
                val dobIndex = c.getColumnIndexOrThrow(COL_DOB)
                val mobilenoIndex = c.getColumnIndexOrThrow(COL_MOBILENO)
                User(
                    name = c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    email = c.getString(c.getColumnIndexOrThrow(COL_EMAIL)),
                    password = c.getString(c.getColumnIndexOrThrow(COL_PASSWORD)),
                    dob = if (c.isNull(dobIndex)) null else c.getString(dobIndex),
                    mobileno = if (c.isNull(mobilenoIndex)) null else c.getString(mobilenoIndex)
                )
            } else null
        }
    }

    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_BOOKS,
            arrayOf(
                COL_ID,
                COL_BOOK_NAME,
                COL_AUTHOR,
                COL_DESCRIPTION,
                COL_PRICE,
                COL_COVER_URI,
                COL_PDF_URI
            ),
            null, null, null, null, null
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                do {
                    val book = Book(
                        id = c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                        bookName = c.getString(c.getColumnIndexOrThrow(COL_BOOK_NAME)),
                        author = c.getString(c.getColumnIndexOrThrow(COL_AUTHOR)),
                        description = c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        price = c.getDouble(c.getColumnIndexOrThrow(COL_PRICE)),
                        coverUri = c.getString(c.getColumnIndexOrThrow(COL_COVER_URI)),
                        pdfUri = c.getString(c.getColumnIndexOrThrow(COL_PDF_URI))
                    )
                    books.add(book)
                } while (c.moveToNext())
            }
        }
        return books
    }

    fun getBookById(id: Int): Book? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_BOOKS WHERE $COL_ID = ?", arrayOf(id.toString()))
        var book: Book? = null
        if (cursor.moveToFirst()) {
            book = Book(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                bookName = cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOK_NAME)),
                author = cursor.getString(cursor.getColumnIndexOrThrow(COL_AUTHOR)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE)),
                coverUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_COVER_URI)),
                pdfUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_PDF_URI))
            )
        }
        cursor.close()
        return book
    }
}