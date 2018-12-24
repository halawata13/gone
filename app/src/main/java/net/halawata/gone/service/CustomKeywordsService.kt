package net.halawata.gone.service

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import net.halawata.gone.entity.KeywordItem
import net.halawata.gone.entity.KeywordType
import java.lang.Exception
import java.net.URLEncoder

class CustomKeywordsService(private val helper: SQLiteOpenHelper) {

    fun getAll(): ArrayList<KeywordItem> {
        val result: ArrayList<KeywordItem> = arrayListOf()
        val db = helper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query("keywords", arrayOf("name"), null, null, null, null, null, null)

            var eol = cursor.moveToFirst()

            while (eol) {
                result.add(KeywordItem(title = cursor.getString(0), type = KeywordType.CUSTOM))
                eol = cursor.moveToNext()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex

        } finally {
            cursor?.close()
            db.close()
        }

        return result
    }

    fun updateAll(keywords: ArrayList<KeywordItem>) {
        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            db.delete("keywords", null, null)

            var id = 1
            keywords.forEach { item ->
                val values = ContentValues()
                values.put("id", id)
                values.put("name", item.title)
                db.insert("keywords", null, values)

                id += 1
            }

            db.setTransactionSuccessful()

        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex

        } finally {
            db.endTransaction()
            db.close()
        }
    }

    companion object {
        fun getUrlString(keyword: String): String {
            val escaped = URLEncoder.encode(keyword, "UTF-8")
            return "https://news.google.com/news/rss/headlines/section/q/$escaped/$escaped?ned=jp&amp;hl=ja&gl=JP"
        }
    }
}
