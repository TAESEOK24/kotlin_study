package com.example.bookmarket;

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.example.bookmarket.mypage.MyPageActivity

class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var index  =0
    lateinit var toolbar: Toolbar
    lateinit var dLayout: DrawerLayout
    lateinit var navigation : NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu_content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        navigation = findViewById(R.id.navigation)
        dLayout =  findViewById(R.id.menu)
        ViewCompat.setOnApplyWindowInsetsListener(navigation) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar.setNavigationOnClickListener {
            dLayout.openDrawer(GravityCompat.START)

        }
        navigation.setNavigationItemSelectedListener(this)


    }


    fun onBookListClick(view: View?){

        Toast.makeText(getApplicationContext(),"도서목록 버튼을 클릭하였습니다",Toast.LENGTH_LONG).show()
        val intent : Intent = Intent(this, BooksActivity::class.java)
        startActivity(intent)
    }
    fun onEBookClick(view: View?){
        Toast.makeText(getApplicationContext(),"E-Book 버튼을 클릭하였습니다",Toast.LENGTH_LONG).show()
    }

    fun onCustomerInquiryClick(view: View?){
        Toast.makeText(getApplicationContext(),"고객문의 버튼을 클릭하였습니다",Toast.LENGTH_LONG).show()
    }

    fun onMyPageClick(view: View?){
        startActivity(Intent(this, MyPageActivity::class.java))
    }

    fun onMarketInfoClick(view: View?){
        Toast.makeText(getApplicationContext(),"마켓정보 버튼을 클릭하였습니다",Toast.LENGTH_LONG).show()
    }
    fun onSettingClick(view: View?){
        Toast.makeText(getApplicationContext(),"설정 버튼을 클릭하였습니다",Toast.LENGTH_LONG).show()
    }

    fun onBannerClick(view: View?){
        val menuObj : ImageView = findViewById(R.id.imageView2)
        if (index %2 == 0) menuObj.setImageResource(R.drawable.menu_banner2)
        else menuObj.setImageResource(R.drawable.menu_banner1)
        index++
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu01 -> {
                Toast.makeText(this, " 메뉴2 : " + item.title, Toast.LENGTH_SHORT).show()

            }
            R.id.menu03 -> {
                startActivity(Intent(this, MyPageActivity::class.java))

            }
            R.id.menu02 -> {
                Toast.makeText(this, " 메뉴3 : " + item.title, Toast.LENGTH_SHORT).show()

            }
        }
        dLayout.closeDrawers()
        return true
    }


}
