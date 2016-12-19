package com.think.mozzo_test

import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.CursorAdapter
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView

import java.util.ArrayList

/**
 * Created by anand on 19/12/16.
 */

class LoaderActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {


    internal var ls: ListView? = null
    internal var aa: ArrayAdapter<Any>? = null!!
    internal var cursorAdapter: CursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loader_activity)
        ls = findViewById(R.id.list_view) as ListView?
        val arrayList = ArrayList<Any>()
        aa = ArrayAdapter(this, R.layout.my_text_view, arrayList)

        cursorAdapter = SimpleCursorAdapter(this, R.layout.loader_layout, null, arrayOf("_ID", "TIME"), intArrayOf(R.id._id, R.id.TIME))
        ls?.adapter = aa
    }


    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor> {
        val loader = CursorLoader(this, UrlHistoryProvider.CONTENT_URI, null, null, null, null)
        return loader
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {

        cursorAdapter?.swapCursor(data)

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }
}
