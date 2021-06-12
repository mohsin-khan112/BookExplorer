package com.example.acropolisassignment.bookBrowsing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.acropolisassignment.R
import com.example.acropolisassignment.db.entity.Genre

class GenreListAdapter(private val context: Context,
                       private var items: List<Genre>):BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = LayoutInflater.from(context).inflate(R.layout.genre_spinner_item, parent, false) as TextView
        textView.text = getItem(position).type
       return textView
    }

    override fun getItem(position: Int): Genre {
       return items[position]
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}