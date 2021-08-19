package net.halawata.gone.service

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import net.halawata.gone.entity.GnewsArticle
import java.lang.Exception

class MuteService(private val helper: SQLiteOpenHelper) {

    fun getAll(): ArrayList<String> {
        val result = arrayListOf<String>()
        val db = helper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query("mutes", arrayOf("name"), null, null, null, null, null, null)

            var eol = cursor.moveToFirst()

            while (eol) {
                result.add(cursor.getString(0))
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

    fun updateAll(keywords: ArrayList<String>) {
        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            db.delete("mutes", null, null)

            var id = 1
            keywords.forEach { title ->
                val values = ContentValues()
                values.put("id", id)
                values.put("name", title)
                db.insert("mutes", null, values)

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

    fun add(hostName: String) {
        val id = getLastId()?.plus(1) ?: 1
        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            val values = ContentValues()
            values.put("id", id)
            values.put("name", hostName)
            db.insert("mutes", null, values)

            db.setTransactionSuccessful()

        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex

        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun filter(articles: ArrayList<GnewsArticle>): ArrayList<GnewsArticle> {
        val mutes = getAll()

        return articles.filter {
            !mutes.contains(it.host)
        } as ArrayList<GnewsArticle>
    }

    private fun getLastId(): Int? {
        val db = helper.readableDatabase
        var result: Int? = null
        var cursor: Cursor? = null

        try {
            cursor = db.query("mutes", arrayOf("id"), null, null, null, null, "id DESC", "1")

            if (cursor.moveToFirst()) {
                result = cursor.getInt(0)
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
}
