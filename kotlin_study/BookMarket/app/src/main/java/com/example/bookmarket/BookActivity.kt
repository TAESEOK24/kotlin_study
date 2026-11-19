package com.example.bookmarket

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookmarket.cart.CartActivity
import com.example.bookmarket.model.Book
import com.example.bookmarket.model.CartRepository

class BookActivity : AppCompatActivity() {

    lateinit var imgObj: ImageView
    lateinit var bookidObj: TextView
    lateinit var nameObj: TextView
    lateinit var priceObj: TextView
    lateinit var dateObj: TextView
    lateinit var writerObj: TextView
    lateinit var descriptionObj: TextView
    lateinit var categoryObj: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_book)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.book)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imgObj = findViewById(R.id.imageView1)
        bookidObj = findViewById(R.id.book_tv01)
        nameObj = findViewById(R.id.book_tv02)
        priceObj = findViewById(R.id.book_tv03)
        dateObj = findViewById(R.id.book_tv04)
        writerObj = findViewById(R.id.book_tv05)
        descriptionObj = findViewById(R.id.book_tv06)
        categoryObj = findViewById(R.id.book_tv07)

        val intent = intent
        bookidObj.text = intent.getStringExtra("bookid")
        nameObj.text = intent.getStringExtra("name")
        priceObj.text = intent.getStringExtra("price")
        dateObj.text = intent.getStringExtra("date")
        writerObj.text = intent.getStringExtra("writer")
        descriptionObj.text = intent.getStringExtra("description")
        categoryObj.text = intent.getStringExtra("category")



        when (bookidObj.text) {
            "BOOK1234" -> {
                imgObj.setImageResource(R.drawable.books_book1)

            }

            "BOOK1235" -> {
                imgObj.setImageResource(R.drawable.books_book2)
            }

            "BOOK1236" -> {
                imgObj.setImageResource(R.drawable.books_book3)
            }

            "BOOK1237" -> {
                imgObj.setImageResource(R.drawable.books_book4)
            }

        }
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        findViewById<Button>(R.id.book_add_to_cart).setOnClickListener {
            val book = Book().apply {
                bookid = bookidObj.text.toString()
                name = nameObj.text.toString()
                price = priceObj.text.toString().toIntOrNull() ?: 0
                date = dateObj.text.toString()
                writer = writerObj.text.toString()
                description = descriptionObj.text.toString()
                category = categoryObj.text.toString()
            }
            CartRepository.addCartItems(book)
            Toast.makeText(this, R.string.cart_added, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_book, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home->{
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            R.id.menu_home -> {
                Toast.makeText(applicationContext,"홈으로 메뉴를 클릭하였습니다", Toast.LENGTH_LONG).show()
                finish()
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_list -> {
                Toast.makeText(applicationContext,"도서목록 메뉴를 클릭하였습니다",Toast.LENGTH_LONG).show()
                finish()
                val intent: Intent = Intent(this, BooksActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_cart -> {
                startActivity(Intent(this, CartActivity::class.java))

                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }


}

