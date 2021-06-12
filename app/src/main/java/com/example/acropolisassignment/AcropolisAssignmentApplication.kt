package com.example.acropolisassignment

import android.app.Application
import android.util.Base64
import android.util.Log
import androidx.room.Room
import com.example.acropolisassignment.db.AppDatabase
import com.example.acropolisassignment.db.entity.Book
import com.example.acropolisassignment.db.entity.Genre
import rx.Single
import rx.SingleSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AcropolisAssignmentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeDatabase()
    }

    private fun initializeDatabase() {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "acropolis-assignment"
        ).build()
        prePopulateDataIntoDatabase()
    }

    private fun prePopulateDataIntoDatabase() {
        Single.create<Boolean> { emitter ->
            try {
                prePopulateGenreData()
                prePopulateBookData()
                emitter.onSuccess(true)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<Boolean>() {
                override fun onSuccess(s: Boolean?) {
                    Log.d(TAG, "onSuccess: ")
                }

                override fun onError(error: Throwable?) {
                    Log.d(TAG, "onError: ${error?.message}")
                }

            })
    }

    private fun prePopulateGenreData() {
        val genreList = mutableListOf<Genre>()
        val genre1 = Genre(1, "Action and Adventure")
        val genre2 = Genre(2, "Classics")
        val genre3 = Genre(3, "Comic")
        val genre4 = Genre(4, "Detective and Mystery")
        val genre5 = Genre(5, "Fantasy")
        val genre6 = Genre(6, "Historical Fiction")
        val genre7 = Genre(7, "Horror")
        val genre8 = Genre(8, "Literary Fiction")
        val genre9 = Genre(9, "Romance")
        val genre10 = Genre(10, "Science Fiction")
        val genre11 = Genre(11, "Suspense and Thrillers")
        val genre12 = Genre(12, "Biographies and Autobiographies")
        val genre13 = Genre(13, "Poetry")
        genreList.add(genre1)
        genreList.add(genre2)
        genreList.add(genre3)
        genreList.add(genre4)
        genreList.add(genre5)
        genreList.add(genre6)
        genreList.add(genre7)
        genreList.add(genre8)
        genreList.add(genre9)
        genreList.add(genre10)
        genreList.add(genre11)
        genreList.add(genre12)
        genreList.add(genre13)
        val genreDAO = database?.genreDAO()
        genreDAO?.insertAll(genreList)
    }

    private fun prePopulateBookData() {
        val bookList = mutableListOf<Book>()
        val book1 = Book(
            id = 1,
            name = "Life of Pi",
            description = "The son of a zookeeper, Pi Patel has an encyclopedic knowledge of animal behavior and a fervent love of stories. When Pi is sixteen, his family emigrates from India to North America aboard a Japanese cargo ship, along with their zoo animals bound for new homes.",
            photo = getBase64FromRawFolderImage(R.raw.life_of_pie),
            genreId = 1
        )
        val book2 = Book(
            id = 2,
            name = "The Three MusKeteers",
            description = "The Three Musketeers is a novel by Alexandre Dumas. Set in the 17th century, it recounts the adventures of a young man named d'Artagnan after he leaves home to travel to Paris, to join the Musketeers of the Guard. D'Artagnan is not one of the musketeers of the title; those being his friends Athos, Porthos and Aramis, inseparable friends who live by the motto \"all for one, one for all\", a motto which is first put forth by d'Artagnan. In genre, The Three Musketeers is primarily a historical novel and adventure",
            photo = getBase64FromRawFolderImage(R.raw.the_three_musketeers),
            genreId = 1
        )
        val book3 = Book(
            id = 3,
            name = "The Call of the Wild",
            description = "The Call of the Wild is a novel by Jack London published in 1903. The story is set in the Yukon during the 1890s Klondike Gold Rush—a period in which strong sled dogs were in high demand. The novel's central character is a dog named Buck, a domesticated dog living at a ranch in the Santa Clara Valley of California as the story opens. Stolen from his home and sold into service as sled dog in Alaska, he reverts to a wild state. Buck is forced to fight in order to dominate other dogs in a harsh climate.",
            photo = getBase64FromRawFolderImage(R.raw.the_call_of_the_wall),
            genreId = 1
        )
        val book4 = Book(
            id = 4,
            name = "The Lightning Thief",
            description = "The Lightning Thief is a light-hearted fantasy about a modern 12-year-old boy who learns that his true father is Poseidon, the Greek god of the sea. Percy sets out to become a hero by undertaking a quest across the United States to find the entrance to the Underworld and stop a war between the gods.",
            photo = getBase64FromRawFolderImage(R.raw.the_lightning_thief),
            genreId = 1
        )
        bookList.add(book1)
        bookList.add(book2)
        bookList.add(book3)
        bookList.add(book4)
        val bookDAO = database?.bookDAO()
        bookDAO?.insertAllBooks(bookList)
    }

    private fun getBase64FromRawFolderImage(imageId: Int): String {
        val inputStream = resources.openRawResource(imageId)
        val bytes = inputStream.readBytes()
        return  Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    companion object {

        private const val TAG = "AcropolisApplication"

        private var database: AppDatabase? = null

        fun getDatabseInstance(): AppDatabase? {
            return database
        }
    }
}