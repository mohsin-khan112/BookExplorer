package com.example.acropolisassignment.note

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.acropolisassignment.R
import com.example.acropolisassignment.db.entity.Note
import com.google.android.material.textfield.TextInputEditText

class AddNotesActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var etNoteTitle: TextInputEditText
    private lateinit var etNoteDescription: TextInputEditText

    private lateinit var context: Context
    private var viewModel: NoteViewModel? = null
    private var uid = 0
    private var bookId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)
        context = this
        uid = intent.getIntExtra("user_id", 0)
        bookId = intent.getIntExtra("book_id", 0)
        toolbar = findViewById(R.id.toolbar_addNote)
        etNoteTitle = findViewById(R.id.et_addNote_title)
        etNoteDescription = findViewById(R.id.et_addNote_description)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        viewModel?.getAddNoteLiveData()?.observe(this,
        Observer {row ->
            if (row != -1L) {
                Toast.makeText(context, "Note added successfully", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(context, "Failed to add Note at this moment", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun addNote(view: View) {
        if (isValidationPassed()) {
            viewModel?.addNote(Note(
                title = etNoteTitle.text.toString(),
                description = etNoteDescription.text.toString(),
                bookId = bookId,
                uid = uid
            ))
        } else {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show()
        }
    }

    private fun isValidationPassed(): Boolean {
        return etNoteTitle.text!!.isNotEmpty() && etNoteDescription.text!!.isNotEmpty()
    }
}