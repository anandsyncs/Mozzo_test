package com.think.mozzo_test

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

/**
 * Created by anand on 18/12/16.
 */


class MaterialAdapter(private val mDataset: ArrayList<String>) : RecyclerView.Adapter<MaterialAdapter.ViewHolder>() {

    class ViewHolder(
            // each data item is just a string in this case

            var mTextView: TextView) : RecyclerView.ViewHolder(mTextView)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MaterialAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.my_text_view, parent, false)

        val vh = ViewHolder(v as TextView)
        return vh
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.mTextView.text = mDataset[position]

    }


    override fun getItemCount(): Int {
        return mDataset.size
    }
}
