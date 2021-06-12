package com.example.acropolisassignment.bookBrowsing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.acropolisassignment.R
import com.example.acropolisassignment.db.entity.Book
import com.example.acropolisassignment.db.entity.GenreBook

class BookListAdapter(

    private val context: Context,
    private var bookList: MutableList<GenreBook>
) : RecyclerView.Adapter<BookListAdapter.BookListAdapterViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(genreBook: GenreBook)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_book_list_item, parent, false)
        return BookListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookListAdapterViewHolder, position: Int) {
        val book = bookList[position].book
        val genre = bookList[position].genre

        //Set cover photo
        val bytes = Base64.decode(book?.photo, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Glide.with(context)
            .asBitmap()
            .load(bitmap)
            .into(holder.ivBookCoverPhoto)

        //Set Book Name
        holder.tvBookName.text = book?.name

        //set genre
        holder.tvGenre.text = genre?.type

        holder.itemView.setOnClickListener {
            listener?.onItemClick(bookList[holder.adapterPosition])
        }
    }

    fun setBookCollection(collection: List<GenreBook>) {
        bookList.clear()
        bookList.addAll(collection)
        notifyDataSetChanged()
    }

    inner class BookListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivBookCoverPhoto: ImageView = itemView.findViewById(R.id.iv_bookItem_coverImage)

        var tvBookName: TextView = itemView.findViewById(R.id.tv_bookItem_name)

        var tvGenre: TextView = itemView.findViewById(R.id.tv_bookItem_genre)
    }

    companion object {
        private const val TAG = "BookListAdapter"
    }
}