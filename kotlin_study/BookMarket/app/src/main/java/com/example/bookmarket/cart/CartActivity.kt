package com.example.bookmarket.cart

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmarket.R
import com.example.bookmarket.model.Book
import com.example.bookmarket.model.CartRepository.cartBooks
import com.example.bookmarket.model.CartRepository.grandTotalCartItems
import com.example.bookmarket.order.ShippingActivity

class CartActivity : AppCompatActivity() {
    private lateinit var cartAdapter: CartAdapter
    private lateinit var allChooseCheckBox: CheckBox
    private lateinit var cartTotalPriceObj: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var orderButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cartTotalPriceObj = findViewById(R.id.cart_total_price)
        allChooseCheckBox = findViewById(R.id.cart_selectall_checkbox)
        emptyView = findViewById(R.id.cart_empty)
        orderButton = findViewById(R.id.cart_order_button)
        recyclerView = findViewById(R.id.cart_recyclerview)
        cartAdapter = CartAdapter(this)
        cartAdapter.setOnSelectChangedListener(object : CartAdapter.OnSelectChangedListener {
            override fun onSelectChanged(item: ArrayList<Book>) {
                refreshRecyclerView()
            }
        })
        recyclerView.adapter = cartAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        orderButton.setOnClickListener { openShipping() }
        allChooseCheckBox.setOnClickListener {
            cartBooks.forEach { it.isCheck = allChooseCheckBox.isChecked }
            refreshRecyclerView()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cart, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
            R.id.order_button -> openShipping()
            R.id.delete_button -> {
                if (cartBooks.isEmpty()) {
                    Toast.makeText(this, R.string.cart_empty, Toast.LENGTH_SHORT).show()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.cart_delete)
                        .setMessage("장바구니를 비우겠습니까?")
                        .setIcon(R.drawable.icon_dialog)
                        .setPositiveButton("예") { _, _ ->
                            cartBooks.clear()
                            refreshRecyclerView()
                        }
                        .setNegativeButton("아니오", null)
                        .show()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun openShipping() {
        if (cartBooks.none { it.isCheck }) {
            Toast.makeText(this, R.string.cart_select_book, Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(Intent(this, ShippingActivity::class.java))
    }

    private fun refreshRecyclerView() {
        cartAdapter.notifyDataSetChanged()
        cartTotalPriceObj.text = getString(R.string.cart_total_price, grandTotalCartItems())
        allChooseCheckBox.isChecked = cartBooks.isNotEmpty() && cartBooks.all { it.isCheck }
        allChooseCheckBox.isEnabled = cartBooks.isNotEmpty()
        emptyView.visibility = if (cartBooks.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (cartBooks.isEmpty()) View.GONE else View.VISIBLE
        orderButton.isEnabled = cartBooks.any { it.isCheck }
    }
}
