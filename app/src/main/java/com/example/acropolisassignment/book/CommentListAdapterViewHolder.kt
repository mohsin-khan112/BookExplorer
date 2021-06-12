package com.example.acropolisassignment.book

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.acropolisassignment.R

class CommentListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var tvPrimaryText = itemView.findViewById<TextView>(R.id.tv_commentItem_userName)

    var tvSecondaryText = itemView.findViewById<TextView>(R.id.tv_commentItem_comment)
}