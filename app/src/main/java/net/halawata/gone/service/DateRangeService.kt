package net.halawata.gone.service

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import net.halawata.gone.entity.GnewsArticle
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class DateRangeService(private val helper: SQLiteOpenHelper) {

    fun get(): DateRange {
        val db = helper.readableDatabase
        var result = ""
        var cursor: Cursor? = null

        try {
            cursor = db.query("configs", arrayOf("config_value"), "config_key = ?", arrayOf("date_range"), null, null, null, null)

            var eol = cursor.moveToFirst()

            while (eol) {
                result = cursor.getString(0)
                eol = cursor.moveToNext()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex

        } finally {
            cursor?.close()
            db.close()
        }

        return DateRange.values()[result.toInt()]
    }

    fun update(dateRange: DateRange) {
        val db = helper.writableDatabase
        db.beginTransaction()

        try {
            val values = ContentValues()
            values.put("config_value", dateRange.ordinal)
            db.update("configs", values, "config_key = ?", arrayOf("date_range"))

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
        val dateRange = get()
        val dateRangeSeconds = getDateRangeSeconds(dateRange) ?: return articles
        val limitDate = Date(System.currentTimeMillis() - dateRangeSeconds * 1000)

        return articles.filter {
            val articleDate = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).parse(it.pubDate)

            articleDate.after(limitDate)
        } as ArrayList<GnewsArticle>
    }

    fun getDateRangeValue(): ArrayList<String> {
        return DateRange.values().map {
            it.rawValue
        } as ArrayList<String>
    }

    private fun getDateRangeSeconds(dateRange: DateRange): Long? {
        return when (dateRange) {
            DateRange.H24 -> 60 * 60 * 24
            DateRange.D3 -> 60 * 60 * 24 * 3
            DateRange.W1 -> 60 * 60 * 24 * 7
            DateRange.M1 -> 60 * 60 * 24 * 30
            DateRange.M3 -> 60 * 60 * 24 * 90
            DateRange.M6 -> 60 * 60 * 24 * 180
            DateRange.Y1 -> 60 * 60 * 24 * 365
            DateRange.UNLIMITED -> null
        }
    }

    enum class DateRange(val rawValue: String) {
        H24("24時間"),
        D3("3日"),
        W1("1週間"),
        M1("1ヶ月"),
        M3("3ヶ月"),
        M6("6ヶ月"),
        Y1("1年"),
        UNLIMITED("無制限"),
    }
}
