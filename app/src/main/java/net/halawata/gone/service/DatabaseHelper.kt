package net.halawata.gone.service

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(val context: Context): SQLiteOpenHelper(context, "gone.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE keywords (id INTEGER PRIMARY KEY, name TEXT)")
        db?.execSQL("CREATE TABLE mutes (id INTEGER PRIMARY KEY, name TEXT)")
        db?.execSQL("CREATE TABLE read_articles (guid TEXT PRIMARY KEY, keyword TEXT)")
        db?.execSQL("CREATE TABLE configs (id INTEGER PRIMARY KEY, config_key TEXT, config_value TEXT)")

        db?.execSQL("INSERT INTO configs(id, config_key, config_value) VALUES(1, 'date_range', '0')")
        db?.execSQL("INSERT INTO configs(id, config_key, config_value) VALUES(2, 'article_limit', '0')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS keywords")
        db?.execSQL("DROP TABLE IF EXISTS mutes")
        db?.execSQL("DROP TABLE IF EXISTS read_articles")
        db?.execSQL("DROP TABLE IF EXISTS configs")

        onCreate(db)
    }
}
