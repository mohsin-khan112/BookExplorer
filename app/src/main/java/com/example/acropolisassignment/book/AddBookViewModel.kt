package com.example.acropolisassignment.book

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.acropolisassignment.AcropolisAssignmentApplication
import com.example.acropolisassignment.db.entity.Book
import com.example.acropolisassignment.db.entity.Genre
import rx.Single
import rx.SingleSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.Exception

class AddBookViewModel: ViewModel() {

    private val genreListLiveData = MutableLiveData<List<Genre>>()

    private val addBookLiveData = MutableLiveData<Long>()

    fun getGenreListLiveData(): LiveData<List<Genre>> {
        return genreListLiveData
    }

    fun getAddBookLiveData(): LiveData<Long?> {
        return addBookLiveData
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

    fun addBook(book: Book) {
        Single.create<Long?> { emitter ->
            val bookDAO = AcropolisAssignmentApplication.getDatabseInstance()?.bookDAO()
            val row = bookDAO?.addBook(book)
            if (row != null && row > -1) {
                emitter.onSuccess(row)
            } else {
                emitter.onError(Exception("Exception while adding book"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<Long?>() {

                override fun onSuccess(value: Long?) {
                    addBookLiveData.value = value
                }

                override fun onError(error: Throwable?) {
                    addBookLiveData.value = null
                }

            })
    }

    companion object {
        private const val TAG = "AddBookViewModel"
    }
}