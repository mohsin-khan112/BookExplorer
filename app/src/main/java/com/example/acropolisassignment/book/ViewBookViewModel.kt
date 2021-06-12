package com.example.acropolisassignment.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.acropolisassignment.AcropolisAssignmentApplication
import com.example.acropolisassignment.db.entity.*
import rx.Single
import rx.SingleSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.Exception

class ViewBookViewModel: ViewModel() {

    private val bookInfoLiveData = MutableLiveData<GenreBook>()

    private val commentsLiveData = MutableLiveData<List<UserComment>>()

    private val addCommentLiveData = MutableLiveData<UserComment?>()

    private val fetchFavouriteGenreLiveData = MutableLiveData<FavouriteGenre?>()

    private val addFavouriteGenreLiveData = MutableLiveData<FavouriteGenre>()

    private val deleteFavouriteGenreLiveData = MutableLiveData<Int>()

    private val fetchFavouriteBookLiveData = MutableLiveData<FavouriteBook?>()

    private val addFavouriteBookLiveData = MutableLiveData<FavouriteBook>()

    private val deleteFavouriteBookLiveData = MutableLiveData<Int>()

    fun getBookInfoLiveData(): LiveData<GenreBook> {
        return bookInfoLiveData
    }

    fun getCommentsLiveData(): LiveData<List<UserComment>?> {
        return commentsLiveData
    }

    fun getAddCommentLiveData(): LiveData<UserComment?> {
        return addCommentLiveData
    }

    fun getFetchFavouriteGenreLiveData(): LiveData<FavouriteGenre?> {
        return fetchFavouriteGenreLiveData
    }

    fun getAddFavouriteGenreLiveData(): LiveData<FavouriteGenre> {
        return addFavouriteGenreLiveData
    }

    fun getDeleteFavouriteGenreLiveData(): LiveData<Int> {
        return deleteFavouriteGenreLiveData
    }

    fun getFetchFavouriteBookLiveData(): LiveData<FavouriteBook?> {
        return fetchFavouriteBookLiveData
    }

    fun getAddFavouriteBookLiveData(): LiveData<FavouriteBook> {
        return addFavouriteBookLiveData
    }

    fun getDeleteFavouriteBookLiveData(): LiveData<Int> {
        return deleteFavouriteBookLiveData
    }

    fun getBookInfo(bookId: Int) {
        Single.create<GenreBook> { emitter ->
            val bookDAO = AcropolisAssignmentApplication.getDatabseInstance()?.bookDAO()
            val genreBook = bookDAO?.getBookInfo(bookId)
            if (genreBook != null) {
                emitter.onSuccess(genreBook)
            } else {
                emitter.onError(Exception("Not able get book info"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<GenreBook>() {
                override fun onSuccess(value: GenreBook) {
                    bookInfoLiveData.value = value
                }

                override fun onError(error: Throwable?) {

                }

            })
    }

    fun fetchComments(bookId: Int) {
        Single.create<List<UserComment>> { emitter ->
            val commentDAO = AcropolisAssignmentApplication.getDatabseInstance()?.commentDAO()
            val comments = commentDAO?.getComments(bookId)
            emitter.onSuccess(comments)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<List<UserComment>?>() {

                override fun onSuccess(commentList: List<UserComment>?) {
                    commentsLiveData.value = commentList
                }

                override fun onError(error: Throwable?) {
                    commentsLiveData.value = null
                }

            })
    }

    fun addComment(comment: Comment) {
        Single.create<UserComment?> { emitter ->

            try {
                val commentDAO = AcropolisAssignmentApplication.getDatabseInstance()?.commentDAO()
                val commentId = commentDAO?.addComment(comment)
                if (commentId != null) {
                    emitter.onSuccess(commentDAO.getCommentById(commentId = commentId.toInt()))
                } else {
                    emitter.onError(Exception("Failed to add comment"))
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<UserComment?>() {
                override fun onSuccess(userComment: UserComment?) {
                    addCommentLiveData.value = userComment
                }

                override fun onError(error: Throwable?) {
                    addCommentLiveData.value = null
                }

            })
    }

    fun checkGenreFavourite(userId: Int, genreId: Int) {
        Single.create<FavouriteGenre?> { emitter ->
            val favouriteGenreDAO = AcropolisAssignmentApplication.getDatabseInstance()?.favouriteGenreDAO()
            val favouriteGenre = favouriteGenreDAO?.isFavouriteGenre(userId, genreId)
            emitter.onSuccess(favouriteGenre)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleSubscriber<FavouriteGenre?>() {

                override fun onSuccess(value: FavouriteGenre?) {
                    fetchFavouriteGenreLiveData.value = value
                }

                override fun onError(error: Throwable?) {
                    fetchFavouriteGenreLiveData.value = null
                }

            })
    }

    fun checkBookFavourite(userId: Int, bookId: Int) {
        Single.create<FavouriteBook?> { emitter ->
            val favouriteBookDAO = AcropolisAssignmentApplication.getDatabseInstance()?.favouriteBookDAO()
            val favouriteGenre = favouriteBookDAO?.isFavouriteBook(userId, bookId)
            emitter.onSuccess(favouriteGenre)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleSubscriber<FavouriteBook?>() {

                override fun onSuccess(value: FavouriteBook?) {
                    fetchFavouriteBookLiveData.value = value
                }

                override fun onError(error: Throwable?) {
                    fetchFavouriteBookLiveData.value = null
                }

            })
    }

    fun addGenreToFavourite(favouriteGenre: FavouriteGenre) {
        Single.create<FavouriteGenre> { emitter ->
            val favouriteGenreDAO = AcropolisAssignmentApplication.getDatabseInstance()?.favouriteGenreDAO()
            val row = favouriteGenreDAO?.addToFavouriteGenre(favouriteGenre)
            if (row != null && row > 0) {
                favouriteGenre.id = row.toInt()
                emitter.onSuccess(favouriteGenre)
            } else {
                emitter.onError(Exception("Exception while making genre favourite"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleSubscriber<FavouriteGenre>() {

                override fun onSuccess(value: FavouriteGenre) {
                    addFavouriteGenreLiveData.value = value
                }

                override fun onError(error: Throwable?) {

                }

            })
    }

    fun addBookToFavourite(favouriteBook: FavouriteBook) {
        Single.create<FavouriteBook> { emitter ->
            val favouriteBookDAO = AcropolisAssignmentApplication.getDatabseInstance()?.favouriteBookDAO()
            val row = favouriteBookDAO?.addToFavouriteBook(favouriteBook)
            if (row != null && row > 0) {
                favouriteBook.id = row.toInt()
                emitter.onSuccess(favouriteBook)
            } else {
                emitter.onError(Exception("Exception while making book favourite"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleSubscriber<FavouriteBook>() {

                override fun onSuccess(value: FavouriteBook) {
                    addFavouriteBookLiveData.value = value
                }

                override fun onError(error: Throwable?) {

                }

            })
    }

    fun deleteGenreFromFavourite(favouriteGenre: FavouriteGenre) {
        Single.create<Int> { emitter ->
            val favouriteGenreDAO = AcropolisAssignmentApplication.getDatabseInstance()?.favouriteGenreDAO()
            val deletedRow = favouriteGenreDAO?.deleteFromFavouriteGenre(favouriteGenre)
            if (deletedRow != null) {
                emitter.onSuccess(deletedRow)
            } else {
                emitter.onError(Exception("Exception while un marking genre as favourite"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<Int>() {

                override fun onSuccess(value: Int) {
                    deleteFavouriteGenreLiveData.value = value
                }

                override fun onError(error: Throwable?) {
                    deleteFavouriteGenreLiveData.value = -1
                }

            })
    }

    fun deleteBookFromFavourite(favouriteBook: FavouriteBook) {
        Single.create<Int> { emitter ->
            val favouriteBookDAO = AcropolisAssignmentApplication.getDatabseInstance()?.favouriteBookDAO()
            val deletedRow = favouriteBookDAO?.deleteFromFavouriteBook(favouriteBook)
            if (deletedRow != null) {
                emitter.onSuccess(deletedRow)
            } else {
                emitter.onError(Exception("Exception while un marking book as favourite"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<Int>() {

                override fun onSuccess(value: Int) {
                    deleteFavouriteBookLiveData.value = value
                }

                override fun onError(error: Throwable?) {
                    deleteFavouriteBookLiveData.value = -1
                }

            })
    }

}