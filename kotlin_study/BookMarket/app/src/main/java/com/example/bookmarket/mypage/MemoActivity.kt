package com.example.bookmarket.mypage

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookmarket.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemoActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHelperMemo
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var adapter: SimpleAdapter
    private val memoList = arrayListOf<HashMap<String, String>>()
    private var selectedMemoId: Int? = null
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)
        db = DatabaseHelperMemo(this)
        searchQuery = savedInstanceState?.getString("query").orEmpty()
        selectedMemoId = savedInstanceState?.getString("selectedId")?.toIntOrNull()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.memo)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        listView = findViewById(R.id.memo_list)
        emptyView = findViewById(R.id.memo_empty)
        adapter = SimpleAdapter(this, memoList, R.layout.activity_memo_item,
            arrayOf("date", "title", "content"), intArrayOf(R.id.tvDate, R.id.tvTitle, R.id.tvContent))
        listView.adapter = adapter
        listView.emptyView = emptyView
        listView.setOnItemClickListener { _, _, position, _ ->
            selectedMemoId = memoList[position]["id"]?.toIntOrNull()
            listView.setItemChecked(position, true)
        }

        findViewById<Button>(R.id.btnInsert).setOnClickListener { showMemoDialog() }
        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            selectedMemo()?.let { showMemoDialog(it) }
        }
        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            val memo = selectedMemo() ?: return@setOnClickListener
            val id = memo.getValue("id").toInt()
            AlertDialog.Builder(this)
                .setTitle("메모장 삭제")
                .setMessage("선택한 메모를 삭제하겠습니까?")
                .setIcon(R.drawable.icon_memo)
                .setPositiveButton("예") { _, _ ->
                    if (db.deleteData(id) > 0) refreshMemos()
                    else Toast.makeText(this, R.string.memo_delete_failed, Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("아니요", null)
                .show()
        }
        refreshMemos(clearSelection = false)
    }

    private fun selectedMemo(): HashMap<String, String>? {
        val memo = memoList.firstOrNull { it["id"]?.toIntOrNull() == selectedMemoId }
        if (memo == null) Toast.makeText(this, R.string.memo_select_item, Toast.LENGTH_SHORT).show()
        return memo
    }

    private fun showMemoDialog(memo: HashMap<String, String>? = null) {
        val view = layoutInflater.inflate(R.layout.activity_memo_dialog, null)
        val title = view.findViewById<EditText>(R.id.title)
        val content = view.findViewById<EditText>(R.id.content)
        val save = view.findViewById<Button>(R.id.insert)
        val id = memo?.getValue("id")?.toInt()
        title.setText(memo?.get("title").orEmpty())
        content.setText(memo?.get("content").orEmpty())
        save.setText(if (id == null) R.string.memo_add else R.string.memo_update)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        save.setOnClickListener {
            val memoTitle = title.text.toString().trim()
            if (memoTitle.isBlank()) {
                title.error = getString(R.string.memo_title_required)
                return@setOnClickListener
            }
            // SimpleDateFormat also supports Android 7 (minSdk 24).
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date())
            val memoContent = content.text.toString()
            val saved = if (id == null) db.insertData(date, memoTitle, memoContent) != -1L
                else db.updateData(date, memoTitle, memoContent, id) > 0
            if (saved) {
                dialog.dismiss()
                refreshMemos()
            } else Toast.makeText(this, R.string.memo_save_failed, Toast.LENGTH_SHORT).show()
        }
        view.findViewById<Button>(R.id.cancel).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun refreshMemos(clearSelection: Boolean = true) {
        if (clearSelection) selectedMemoId = null
        val memos = if (searchQuery.isBlank()) db.readData() else db.searchReadData(searchQuery)
        memoList.clear()
        memoList.addAll(memos)
        listView.clearChoices()
        adapter.notifyDataSetChanged()
        val position = memoList.indexOfFirst { it["id"]?.toIntOrNull() == selectedMemoId }
        if (position >= 0) listView.setItemChecked(position, true) else selectedMemoId = null
        emptyView.setText(if (searchQuery.isBlank()) R.string.memo_empty else R.string.memo_no_results)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.memo_search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQuery = query.orEmpty().trim()
                refreshMemos()
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText.orEmpty().trim()
                if (searchQuery != query) {
                    searchQuery = query
                    refreshMemos()
                }
                return true
            }
        })
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean = true
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                searchQuery = ""
                refreshMemos()
                return true
            }
        })
        if (searchQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(searchQuery, false)
            searchView.clearFocus()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("query", searchQuery)
        outState.putString("selectedId", selectedMemoId?.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}
