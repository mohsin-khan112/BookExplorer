package com.example.acropolisassignment.note

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.acropolisassignment.R
import com.example.acropolisassignment.book.CommentListAdapterViewHolder
import com.example.acropolisassignment.db.entity.Note

class NoteListAdapter(
    private val context: Context,
    private val notes: List<Note>
) : RecyclerView.Adapter<CommentListAdapterViewHolder>() {

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentListAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_comment_list_item, parent, false)
        return CommentListAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentListAdapterViewHolder, position: Int) {
        val note = notes[position]
        holder.tvPrimaryText.text = note.title
        holder.tvSecondaryText.text = note.description
    }
}