package com.example.acropolisassignment.bookBrowsing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.acropolisassignment.R
import com.example.acropolisassignment.book.AddBookActivity
import com.example.acropolisassignment.book.ViewBookActivity
import com.example.acropolisassignment.db.entity.Book
import com.example.acropolisassignment.db.entity.Genre
import com.example.acropolisassignment.db.entity.GenreBook
import com.example.acropolisassignment.user.SignInActivity
import com.example.acropolisassignment.user.SignUpActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class BookBrowsingActivity : AppCompatActivity(), BookListAdapter.OnItemClickListener {

    private lateinit var toolbar: Toolbar
    private lateinit var tvSelectGenreTitle: TextView
    private lateinit var genreSpinner: Spinner
    private lateinit var bookRecyclerView: RecyclerView
    private lateinit var tvNoBookFoundMessage: TextView
    private lateinit var tvNoBookFoundDescription: TextView
    private lateinit var fabAddBook: ExtendedFloatingActionButton

    private lateinit var noBookFoundForGenreMessage: String
    private lateinit var noBookFoundForSearchMessage: String

    private var signInItem: MenuItem? = null
    private var signupItem: MenuItem? = null
    private var signOutItem: MenuItem? = null

    private lateinit var context: Context
    private var viewModel: BookBrowsingViewModel? = null

    private var genreList: List<Genre>? = null
    private var genreListAdapter: GenreListAdapter? = null
    private var selectedGenre: Genre? = null
    private var bookListAdapter: BookListAdapter? = null

    private var query: String? = null
    private var isSearchExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_browsing)
        toolbar = findViewById(R.id.toolbar_bookBrowsing)
        tvSelectGenreTitle = findViewById(R.id.tv_bookBrowsing_selectGenreTitle)
        genreSpinner = findViewById(R.id.spinner_bookBrowsing_genreList)
        bookRecyclerView = findViewById(R.id.rv_bookBrowsing_bookList)
        tvNoBookFoundMessage = findViewById(R.id.tv_bookBrowsingItem_noBookFoundMessage)
        tvNoBookFoundDescription = findViewById(R.id.tv_bookBrowsingItem_noBookFoundDescription)
        fabAddBook = findViewById(R.id.fab_bookBrowsing_addBook)

        noBookFoundForGenreMessage = resources?.getString(R.string.no_book_found_for_genre)!!
        noBookFoundForSearchMessage = resources?.getString(R.string.no_book_found_for_search)!!

        context = this
        setSupportActionBar(toolbar)
        supportActionBar?.title = context.resources.getString(R.string.browse_book_screen_label)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        setupBookRecyclerView()
        setupViewModel()
    }

    private fun setupBookRecyclerView() {
        bookListAdapter = BookListAdapter(context, mutableListOf())
        bookListAdapter?.setOnItemClickListener(this)
        bookRecyclerView.layoutManager = GridLayoutManager(context, 2)
        bookRecyclerView.adapter = bookListAdapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BookBrowsingViewModel::class.java)
        viewModel?.fetchGenreList()
        viewModel?.getGenreListLiveData()?.observe(this,
            Observer<List<Genre>> { genreList ->
                Log.d(TAG, "setupViewModel: Genre list size : ${genreList.size}")
                this.genreList = genreList
                setupGenreSpinner(genreList)
            })

        viewModel?.getBookListLiveData()?.observe(this,
            Observer<List<GenreBook>> { bookList ->
                Log.d(TAG, "setupViewModel: Book List size : ${bookList.size}")
                if (bookList.isEmpty()) {
                    tvNoBookFoundDescription.visibility = View.VISIBLE
                    tvNoBookFoundMessage.visibility = View.VISIBLE
                    bookRecyclerView.visibility = View.GONE
                    handleEmptyMessage()
                } else {
                    tvNoBookFoundDescription.visibility = View.GONE
                    tvNoBookFoundMessage.visibility = View.GONE
                    bookRecyclerView.visibility = View.VISIBLE
                    bookListAdapter?.setBookCollection(bookList)
                }
            })
    }

    private fun setupGenreSpinner(genreList: List<Genre>) {
        genreListAdapter = GenreListAdapter(context, genreList)
        genreSpinner.adapter = genreListAdapter
        genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedGenre = genreList[position]
                fetchBooksByGenre()
            }

        }
    }

    private fun fetchBooksByGenre() {
        if (selectedGenre?.id != null) {
            viewModel?.fetchBookListByGenre(selectedGenre?.id!!)
        }
    }

    private fun searchBookByName() {
        viewModel?.searchBookByName(query ?: "")
    }

    private fun handleEmptyMessage() {
        val message: String
        if (isSearchExpanded) {
            message = String.format(noBookFoundForSearchMessage, query)
        } else {
            message = String.format(noBookFoundForGenreMessage, selectedGenre?.type)

        }
        tvNoBookFoundMessage.text = message
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_book_browsing, menu)
        signInItem = menu?.findItem(R.id.menuItem_signIn)
        signupItem = menu?.findItem(R.id.menuItem_signUp)
        signOutItem = menu?.findItem(R.id.menuItem_signOut)
        handleViewVisibility()
        val searchItem = menu?.findItem(R.id.menuItem_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = resources?.getString(R.string.search_book_hint)
        setSearchViewExpandCollapseListener(searchItem)
        setSearchViewTextChangeListener(searchView)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItem_signUp -> {
                val intent = Intent(context, SignUpActivity::class.java)
                startActivityForResult(intent, USER_SCREEN_CODE)
            }

            R.id.menuItem_signIn -> {
                val intent = Intent(context, SignInActivity::class.java)
                startActivityForResult(intent, USER_SCREEN_CODE)
            }

            R.id.menuItem_signOut -> {
                val pref = getSharedPreferences("acropolis-pref", Context.MODE_PRIVATE)
                pref.edit()
                    .putBoolean("login_status", false)
                    .putInt("user_id", 0)
                    .apply()
                handleViewVisibility()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setSearchViewExpandCollapseListener(searchMenuItem: MenuItem?) {

        searchMenuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                signInItem?.isVisible = false
                signupItem?.isVisible = false
                signOutItem?.isVisible = false
                tvSelectGenreTitle.visibility = View.GONE
                genreSpinner.visibility = View.GONE
                isSearchExpanded = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                signInItem?.isVisible = true
                signupItem?.isVisible = true
                tvSelectGenreTitle.visibility = View.VISIBLE
                genreSpinner.visibility = View.VISIBLE
                isSearchExpanded = false
                handleViewVisibility()
                fetchBooksByGenre()
                return true
            }

        })
    }

    private fun setSearchViewTextChangeListener(searchView: SearchView?) {
        searchView?.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener,

            SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                query = newText
                searchBookByName()
                return true
            }

        })
    }

    override fun onItemClick(genreBook: GenreBook) {
        val intent = Intent(context, ViewBookActivity::class.java)
        intent.putExtra("book_id", genreBook.book?.id)
        startActivity(intent)
    }

    fun addBook(view: View) {
        val intent = Intent(context, AddBookActivity::class.java)
        startActivityForResult(intent, ADD_BOOK_SCREEN_CODE)
    }

    private fun handleViewVisibility() {
        val pref = getSharedPreferences("acropolis-pref", Context.MODE_PRIVATE)
        val loginStatus = pref.getBoolean("login_status", false)
        if (loginStatus) {
            signInItem?.isVisible = false
            signupItem?.isVisible = false
            signOutItem?.isVisible = true
            fabAddBook.show()
        } else {
            signInItem?.isVisible = true
            signupItem?.isVisible = true
            signOutItem?.isVisible = false
            fabAddBook.hide()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && data.getIntExtra("user_id", 0) != 0) {
            when (requestCode) {

                USER_SCREEN_CODE -> {
                    val pref = getSharedPreferences("acropolis-pref", Context.MODE_PRIVATE)
                    pref.edit()
                            .putBoolean("login_status", true)
                            .putInt("user_id", data?.getIntExtra("user_id", 0) ?: 0)
                            .apply()
                    handleViewVisibility()
                }

                ADD_BOOK_SCREEN_CODE -> {
                    fetchBooksByGenre()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val TAG = "BookBrowsingActivity"
        const val USER_SCREEN_CODE = 100
        const val ADD_BOOK_SCREEN_CODE = 200
    }
}