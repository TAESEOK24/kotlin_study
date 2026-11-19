package com.example.bookmarket.mypage

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelperMemo(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        const val DATABASE_NAME = "Memo.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "MemoTABLE"
        const val KEY_ID = "id"
        const val KEY_DATE = "date"
        const val KEY_TITLE = "title"
        const val KEY_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$KEY_DATE TEXT, $KEY_TITLE TEXT, $KEY_CONTENT TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Version 1 is the current schema; keep existing memos intact.
    }

    private fun values(date: String, title: String, content: String) = ContentValues().apply {
        put(KEY_DATE, date)
        put(KEY_TITLE, title)
        put(KEY_CONTENT, content)
    }

    fun insertData(date: String, title: String, content: String): Long =
        writableDatabase.insert(TABLE_NAME, null, values(date, title, content))

    fun updateData(date: String, title: String, content: String, id: Int): Int =
        writableDatabase.update(TABLE_NAME, values(date, title, content), "$KEY_ID = ?", arrayOf(id.toString()))

    fun deleteData(id: Int): Int =
        writableDatabase.delete(TABLE_NAME, "$KEY_ID = ?", arrayOf(id.toString()))

    fun deleteAllData() {
        writableDatabase.delete(TABLE_NAME, null, null)
    }

    private fun Cursor.memo(): HashMap<String, String> = hashMapOf(
        KEY_ID to getInt(getColumnIndexOrThrow(KEY_ID)).toString(),
        KEY_DATE to (getString(getColumnIndexOrThrow(KEY_DATE)) ?: ""),
        KEY_TITLE to (getString(getColumnIndexOrThrow(KEY_TITLE)) ?: ""),
        KEY_CONTENT to (getString(getColumnIndexOrThrow(KEY_CONTENT)) ?: "")
    )

    private fun queryMemos(selection: String? = null, args: Array<String>? = null): ArrayList<HashMap<String, String>> {
        val memos = arrayListOf<HashMap<String, String>>()
        readableDatabase.query(TABLE_NAME, null, selection, args, null, null, "$KEY_ID ASC").use { cursor ->
            while (cursor.moveToNext()) memos.add(cursor.memo())
        }
        return memos
    }

    fun readData(): ArrayList<HashMap<String, String>> = queryMemos()

    fun searchReadData(content: String): ArrayList<HashMap<String, String>> {
        // Treat SQL wildcard characters as literal search text.
        val escaped = content.replace("!", "!!").replace("%", "!%").replace("_", "!_")
        val pattern = "%$escaped%"
        return queryMemos("($KEY_TITLE LIKE ? ESCAPE '!' OR $KEY_CONTENT LIKE ? ESCAPE '!')",
            arrayOf(pattern, pattern))
    }

    fun searchReadData2(id: Int): HashMap<String, String> {
        readableDatabase.query(TABLE_NAME, null, "$KEY_ID = ?", arrayOf(id.toString()), null, null, null).use { cursor ->
            return if (cursor.moveToFirst()) cursor.memo() else hashMapOf()
        }
    }
}
