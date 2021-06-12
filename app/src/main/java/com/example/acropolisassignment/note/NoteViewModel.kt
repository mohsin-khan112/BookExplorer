package com.example.acropolisassignment.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.acropolisassignment.AcropolisAssignmentApplication
import com.example.acropolisassignment.db.entity.Note
import rx.Scheduler
import rx.Single
import rx.SingleSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.Exception

class NoteViewModel: ViewModel() {

    private val addNoteLiveData = MutableLiveData<Long>()

    private val fetchNotesLiveData = MutableLiveData<List<Note>>()

    fun getAddNoteLiveData(): LiveData<Long> {
        return addNoteLiveData
    }

    fun getFetchNotesLiveData(): LiveData<List<Note>> {
        return fetchNotesLiveData
    }

    fun addNote(note: Note) {
        Single.create<Long> { emitter ->
            val noteDAO = AcropolisAssignmentApplication.getDatabseInstance()?.noteDAO()
            val row = noteDAO?.addNote(note)
            if (row != null && row > 0) {
                emitter.onSuccess(row)
            } else {
                emitter.onError(Exception("Failed to add Note"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<Long>() {

                override fun onSuccess(value: Long) {
                    addNoteLiveData.value = value
                }

                override fun onError(error: Throwable?) {
                    addNoteLiveData.value = -1
                }

            })
    }

    fun fetchNotes(uid: Int, bookId: Int) {
        Single.create<List<Note>> { emitter ->
            val noteDAO = AcropolisAssignmentApplication.getDatabseInstance()?.noteDAO()
            val notes = noteDAO?.getNotes(uid, bookId)
            if (notes.isNullOrEmpty()) {
                emitter.onError(Exception("Exception while fetching Notes"))
            } else {
                emitter.onSuccess(notes)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleSubscriber<List<Note>>() {

                override fun onSuccess(value: List<Note>) {
                    fetchNotesLiveData.value = value
                }

                override fun onError(error: Throwable?) {
                    fetchNotesLiveData.value = mutableListOf()
                }

            })
    }
}