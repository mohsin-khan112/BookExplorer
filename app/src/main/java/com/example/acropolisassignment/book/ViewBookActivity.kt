package com.example.acropolisassignment.book

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.acropolisassignment.R
import com.example.acropolisassignment.db.entity.Comment
import com.example.acropolisassignment.db.entity.FavouriteBook
import com.example.acropolisassignment.db.entity.FavouriteGenre
import com.example.acropolisassignment.db.entity.GenreBook
import com.example.acropolisassignment.note.AddNotesActivity
import com.example.acropolisassignment.note.ViewNotesActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewBookActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var ivCoverPhoto: ImageView
    lateinit var tvBooName: TextView
    lateinit var tvGenreType: TextView
    lateinit var tvBookDescription: TextView
    lateinit var tvNoComments: TextView
    lateinit var etComment: EditText
    lateinit var groupComment: Group
    lateinit var groupButtons: Group
    lateinit var rvCommentList: RecyclerView
    lateinit var fabFavouriteGenre: FloatingActionButton
    lateinit var fabFavouriteBook: FloatingActionButton

    lateinit var emptyFavouriteGenreIcon: Drawable
    lateinit var filledFavouriteGenreIcon: Drawable

    lateinit var context: Context
    private var genreBook: GenreBook? = null
    private var bookId: Int = 0
    private var uid: Int = 0
    private var viewModel: ViewBookViewModel? = null
    private var commentListAdapter: CommentListAdapter? = null
    private var isFavouriteBook = false
    private var isFavouriteGenre = false
    private lateinit var favouriteGenre: FavouriteGenre
    private lateinit var favouriteBook: FavouriteBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_book)
        //genreBook = intent.getSerializableExtra("selected_book") as? GenreBook
        bookId = intent.getIntExtra("book_id", 0)
        uid = getSharedPreferences("acropolis-pref", Context.MODE_PRIVATE).getInt("user_id", 0)
        toolbar = findViewById(R.id.toolbar_viewBook)
        ivCoverPhoto = findViewById(R.id.iv_viewBook_coverImage)
        tvBooName = findViewById(R.id.tv_viewBook_name)
        tvGenreType = findViewById(R.id.tv_viewBook_genreType)
        tvBookDescription = findViewById(R.id.tv_viewBook_description)
        tvNoComments = findViewById(R.id.tv_viewBook_noComments)
        etComment = findViewById(R.id.et_viewBook_commentBox)
        groupComment = findViewById(R.id.group_viewBook_comment)
        groupButtons = findViewById(R.id.group_viewBook_buttons)
        rvCommentList = findViewById(R.id.rv_viewBook_commentList)
        fabFavouriteGenre = findViewById(R.id.fab_viewBook_markGenreFavourite)
        fabFavouriteBook = findViewById(R.id.fab_viewBook_markBookFavourite)
        context = this
        emptyFavouriteGenreIcon = context.resources.getDrawable(R.drawable.ic_favorite_empty)
        emptyFavouriteGenreIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        filledFavouriteGenreIcon = context.resources.getDrawable(R.drawable.ic_favourite_filled)
        filledFavouriteGenreIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupFabIcon()
        setupViewModel()
    }

    private fun setupBookData() {
        tvBooName.text = genreBook?.book?.name
        tvBookDescription.text = genreBook?.book?.description
        tvGenreType.text = genreBook?.genre?.type
        setCoverPhoto()
        if (uid == 0) {
            groupComment.visibility = View.GONE
            groupButtons.visibility = View.GONE
        } else {
            groupComment.visibility = View.VISIBLE
            groupButtons.visibility = View.VISIBLE
        }
        setupCommentListRecyclerView()
    }

    private fun setCoverPhoto() {
        val bytes = Base64.decode(genreBook?.book?.photo, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Glide.with(context)
            .asBitmap()
            .load(bitmap)
            .into(ivCoverPhoto)
    }

    private fun setupCommentListRecyclerView() {
        commentListAdapter = CommentListAdapter(context, mutableListOf())
        rvCommentList.layoutManager = LinearLayoutManager(context)
        rvCommentList.adapter = commentListAdapter
    }

    private fun setupFabIcon() {
        if (isFavouriteBook) {
            fabFavouriteBook.setImageDrawable(filledFavouriteGenreIcon)
        } else {
            fabFavouriteBook.setImageDrawable(emptyFavouriteGenreIcon)
        }
        fabFavouriteBook.drawable.mutate()
        if (isFavouriteGenre) {
            fabFavouriteGenre.setImageDrawable(filledFavouriteGenreIcon)
        } else {
            fabFavouriteGenre.setImageDrawable(emptyFavouriteGenreIcon)
        }
        fabFavouriteGenre.drawable.mutate()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(ViewBookViewModel::class.java)
        viewModel?.getBookInfoLiveData()?.observe(this,
        Observer {genreBook ->
            this.genreBook = genreBook
            setupBookData()
            viewModel?.fetchComments(genreBook?.book?.id ?: 0)
            viewModel?.checkGenreFavourite(uid, genreBook?.book?.genreId!!)
            viewModel?.checkBookFavourite(uid, genreBook?.book?.id!!)
        })
        viewModel?.getCommentsLiveData()?.observe(this,
            Observer { commentList ->
                if (commentList.isNullOrEmpty()) {
                    tvNoComments.visibility = View.VISIBLE
                    rvCommentList.visibility = View.GONE
                } else {
                    tvNoComments.visibility = View.GONE
                    rvCommentList.visibility = View.VISIBLE
                    commentListAdapter?.setCollection(commentList)
                }
            })
        viewModel?.getAddCommentLiveData()?.observe(this,
            Observer { comment ->
                if (comment != null) {
                    if (rvCommentList.visibility == View.GONE) {
                        rvCommentList.visibility = View.VISIBLE
                    }
                    if (tvNoComments.visibility == View.VISIBLE) {
                        tvNoComments.visibility = View.GONE
                    }
                    commentListAdapter?.addComment(comment)
                } else {
                    Toast.makeText(context, "Failed to add comment", Toast.LENGTH_LONG).show()
                }
            })
        viewModel?.getFetchFavouriteGenreLiveData()?.observe(this,
            Observer { favouriteGenre ->
                if (favouriteGenre != null) {
                    isFavouriteGenre = true
                    this.favouriteGenre = favouriteGenre
                } else {
                    isFavouriteGenre = false
                }
                setupFabIcon()
            })
        viewModel?.getFetchFavouriteBookLiveData()?.observe(this,
            Observer { favouriteBook ->
                if (favouriteBook != null) {
                    isFavouriteBook = true
                    this.favouriteBook = favouriteBook
                } else {
                    isFavouriteBook = false
                }
                setupFabIcon()
            })
        viewModel?.getAddFavouriteGenreLiveData()?.observe(this,
            Observer { favouriteGenre ->
                this.favouriteGenre = favouriteGenre
                isFavouriteGenre = true
                setupFabIcon()
            })
        viewModel?.getAddFavouriteBookLiveData()?.observe(this,
            Observer { favouriteBook ->
                this.favouriteBook = favouriteBook
                isFavouriteBook = true
                setupFabIcon()
            })
        viewModel?.getDeleteFavouriteGenreLiveData()?.observe(this,
            Observer { deletedRow ->
                if (deletedRow == -1) {
                    Toast.makeText(
                        context,
                        "failed to unmark genre as favourite",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    isFavouriteGenre = false
                    setupFabIcon()
                }
            })
        viewModel?.getDeleteFavouriteBookLiveData()?.observe(this,
            Observer { deletedRow ->
                if (deletedRow == -1) {
                    Toast.makeText(
                        context,
                        "failed to unmark book as favourite",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    isFavouriteBook = false
                    setupFabIcon()
                }
            })

        viewModel?.getBookInfo(bookId)
    }

    fun addNote(view: View) {
        val intent = Intent(context, AddNotesActivity::class.java)
        intent.putExtra("user_id", uid)
        intent.putExtra("book_id", genreBook?.book?.id)
        startActivity(intent)
    }
    fun viewNote(view: View) {
        val intent = Intent(context, ViewNotesActivity::class.java)
        intent.putExtra("user_id", uid)
        intent.putExtra("book_id", genreBook?.book?.id)
        startActivity(intent)
    }

    fun markBookFavourite(view: View) {
        if (isFavouriteBook) {
            viewModel?.deleteBookFromFavourite(favouriteBook)
        } else {
            viewModel?.addBookToFavourite(
                FavouriteBook(
                    userId = uid,
                    bookId = genreBook?.book?.id!!
                )
            )
        }
    }

    fun markGenreFavourite(view: View) {
        if (isFavouriteGenre) {
            viewModel?.deleteGenreFromFavourite(favouriteGenre)
        } else {
            viewModel?.addGenreToFavourite(
                FavouriteGenre(
                    userId = uid,
                    genreId = genreBook?.genre?.id!!
                )
            )
        }
    }

    fun addComment(view: View) {
        if (etComment.text.isNotEmpty()) {
            viewModel?.addComment(
                Comment(
                    text = etComment.text!!.toString(),
                    bookId = genreBook?.book?.id!!,
                    userId = uid
                )
            )
            etComment.setText("")
        } else {
            Toast.makeText(context, "Please type comment", Toast.LENGTH_LONG).show()
        }
    }
}