package com.example.acropolisassignment.note

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.acropolisassignment.R

class ViewNotesActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var rvNotesList: RecyclerView
    private lateinit var tvNoNotes: TextView

    private lateinit var context: Context
    private var viewModel: NoteViewModel? = null
    private var uid = 0
    private var bookId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_notes)
        uid = intent.getIntExtra("user_id", 0)
        bookId = intent.getIntExtra("book_id", 0)
        toolbar = findViewById(R.id.toolbar_viewNotes)
        context = this
        toolbar = findViewById(R.id.toolbar_viewNotes)
        rvNotesList = findViewById(R.id.rv_viewNotes_notesList)
        tvNoNotes = findViewById(R.id.tv_viewNotes_noNotes)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        viewModel?.getFetchNotesLiveData()?.observe(this,
        Observer { notes ->
            if (notes.isNullOrEmpty()) {
                tvNoNotes.visibility = View.VISIBLE
                rvNotesList.visibility = View.GONE
            } else {
                tvNoNotes.visibility = View.GONE
                rvNotesList.visibility = View.VISIBLE
                val adapter = NoteListAdapter(context, notes)
                rvNotesList.layoutManager = LinearLayoutManager(context)
                rvNotesList.adapter = adapter
            }
        })
        viewModel?.fetchNotes(uid, bookId)
    }
}