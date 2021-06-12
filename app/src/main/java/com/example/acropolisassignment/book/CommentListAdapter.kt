package com.example.acropolisassignment.book

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acropolisassignment.R
import com.example.acropolisassignment.db.entity.UserComment

class CommentListAdapter(
    private val context: Context,
    private var commentList: MutableList<UserComment>
) : RecyclerView.Adapter<CommentListAdapterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentListAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_comment_list_item, parent, false)
        return CommentListAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentListAdapterViewHolder, position: Int) {
        val userComment = commentList[position]
        holder.tvPrimaryText.text = userComment.user?.firstName?.plus(" ")?.plus(userComment.user?.lastName)
        holder.tvSecondaryText.text = userComment.comment?.text
    }

    fun addComment(comment: UserComment) {
        commentList.add(comment)
        notifyItemInserted(commentList.size - 1)
    }

    fun setCollection(collection: List<UserComment>) {
        commentList.clear()
        commentList.addAll(collection)
        notifyDataSetChanged()
    }
}