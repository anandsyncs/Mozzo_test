package com.think.mozzo_test


import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

import java.util.HashMap

/**
 * Created by anand on 19/12/16.
 */

class UrlHistoryProvider : ContentProvider() {

    /**
     * Database specific constant declarations
     */

    private var db: SQLiteDatabase? = null

    private class DatabaseHelper internal constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE_NAME)
            onCreate(db)
        }
    }

    override fun onCreate(): Boolean {
        val context = context
        val dbHelper = DatabaseHelper(context)


        db = dbHelper.writableDatabase
        return if (db == null) false else true
    }

    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?, s1: String?): Cursor? {
        var s1 = s1
        val qb = SQLiteQueryBuilder()
        qb.tables = HISTORY_TABLE_NAME

        qb.setProjectionMap(HISTORY_PROJECTION_MAP)
        if (s1 == null || s1 === "") {
            /**
             * By default sort on student names
             */
            s1 = TIME
        }

        val c = qb.query(db, strings, s,
                strings1, null, null, s1)
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val rowID = db!!.insert(HISTORY_TABLE_NAME, "", contentValues)

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            val _uri = ContentUris.withAppendedId(CONTENT_URI, rowID)
            context!!.contentResolver.notifyChange(_uri, null)
            return _uri
        }

        throw SQLException("Failed to add a record into " + uri)
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        return 0
    }

    companion object {

        internal val PROVIDER_NAME = "com.think.mozzo_test.UrlHistoryProvider"
        internal val URL = "content://$PROVIDER_NAME/history"
        internal val CONTENT_URI = Uri.parse(URL)

        internal val _ID = "_id"
        internal val TIME = "time"

        internal val URLS = 1
        internal val URL_ID = 2

        private val HISTORY_PROJECTION_MAP: HashMap<String, String>? = null

        internal val uriMatcher: UriMatcher

        init {
            uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher.addURI(PROVIDER_NAME, "history", URLS)
            uriMatcher.addURI(PROVIDER_NAME, "history/#", URL_ID)
        }

        internal val DATABASE_NAME = "Beacon"
        internal val HISTORY_TABLE_NAME = "history"
        internal val DATABASE_VERSION = 1
        internal val CREATE_DB_TABLE =
                " CREATE TABLE " + HISTORY_TABLE_NAME +
                        " (_id TEXT , " +
                        " TIME TEXT NOT NULL);"
    }
}
