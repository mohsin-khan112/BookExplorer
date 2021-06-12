package com.example.acropolisassignment.bookBrowsing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.acropolisassignment.AcropolisAssignmentApplication
import com.example.acropolisassignment.db.entity.Book
import com.example.acropolisassignment.db.entity.Genre
import com.example.acropolisassignment.db.entity.GenreBook
import rx.Single
import rx.SingleSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.Exception

class BookBrowsingViewModel: ViewModel() {

    private val genreListLiveData = MutableLiveData<List<Genre>>()
    private val bookListLiveData = MutableLiveData<List<GenreBook>>()

    fun getGenreListLiveData(): LiveData<List<Genre>> {
        return genreListLiveData
    }

    fun getBookListLiveData(): LiveData<List<GenreBook>> {
        return bookListLiveData
    }

    fun fetchGenreList() {
        Single.create<List<Genre>> { emitter ->
            val genreDAO = AcropolisAssignmentApplication.getDatabseInstance()?.genreDAO()
            val genreList = genreDAO?.getAllGenre()
            if (genreList != null) {
                emitter.onSuccess(genreList)
            } else {
                emitter.onError(Exception("Exception while fetching genre list"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<List<Genre>>() {

                override fun onSuccess(list: List<Genre>?) {
                    genreListLiveData.value = list
                }

                override fun onError(error: Throwable?) {
                    Log.e(TAG, "onError while fetching genre list : ${error?.message}")
                }

            })
    }

    fun fetchBookListByGenre(genreId: Int) {
        Single.create<List<GenreBook>> { emitter ->
            val bookDAO = AcropolisAssignmentApplication.getDatabseInstance()?.bookDAO()
            if (bookDAO != null) {
                emitter.onSuccess(bookDAO.getBooksByGenre(genreId))
            } else {
                emitter.onError(Exception("Exception while fetching books"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(BookListSingleSubscriber())
    }

    fun searchBookByName(query: String) {
        Single.create<List<GenreBook>> { emitter ->
            val bookDAO = AcropolisAssignmentApplication.getDatabseInstance()?.bookDAO()
            if (bookDAO != null) {
                emitter.onSuccess(bookDAO.getBooksByName(query.plus("%")))
            } else {
                emitter.onError(Exception("Exception while searching books"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(BookListSingleSubscriber())
    }

    private inner class BookListSingleSubscriber: SingleSubscriber<List<GenreBook>>() {
        override fun onSuccess(bookList: List<GenreBook>?) {
            bookListLiveData.value = bookList
        }

        override fun onError(error: Throwable?) {
            Log.e(TAG, "onError inside fetchBookList() : Error ${error?.message}")
        }

    }

    companion object {
        private const val TAG = "BookBrowsingViewModel"
    }
}