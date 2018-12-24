package net.halawata.gone.service

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import net.halawata.gone.entity.Article
import java.lang.Exception

class ReadArticleService(private val helper: SQLiteOpenHelper) {

    fun readArticle(article: Article, keyword: String) {
        if (article.isRead) {
            return
        }

        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            val values = ContentValues()
            values.put("guid", article.guid)
            values.put("keyword", keyword)
            db.insert("read_article", null, values)

            db.setTransactionSuccessful()

        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex

        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun <T: Article> checkReadArticle(articles: ArrayList<T>, keyword: String): ArrayList<T> {
        val guids = mutableListOf<String>()
        val db = helper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query("read_article", arrayOf("guid"), "keyword = ?", arrayOf(keyword), null, null, null, null)

            var eol = cursor.moveToFirst()

            while (eol) {
                guids.add(cursor.getString(0))
                eol = cursor.moveToNext()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex

        } finally {
            cursor?.close()
            db.close()
        }

        return articles.map {
            it.isRead = guids.contains(it.guid)
            it
        } as ArrayList<T>
    }

    fun deleteKeywords(keywords: List<String>) {
        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            for (keyword in keywords) {
                db.delete("read_article", "keyword = ?", arrayOf(keyword))
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
}
