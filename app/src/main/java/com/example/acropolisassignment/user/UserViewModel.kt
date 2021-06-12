package com.example.acropolisassignment.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.acropolisassignment.AcropolisAssignmentApplication
import com.example.acropolisassignment.db.entity.User
import rx.Scheduler
import rx.Single
import rx.SingleSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.Exception

class UserViewModel : ViewModel() {

    private var signUpLiveData = MutableLiveData<Long>()

    private var signInLiveData = MutableLiveData<User?>()

    fun getSignUpLiveData(): LiveData<Long> {
        return signUpLiveData
    }

    fun getSignInLiveData(): LiveData<User?> {
        return signInLiveData
    }

    fun singUp(user: User) {
        Single.create<Long> { emitter ->
            val userDAO = AcropolisAssignmentApplication.getDatabseInstance()?.userDAO()
            if (userDAO != null) {
                val row = userDAO.insertUser(user)
                if (row > -1) {
                    emitter.onSuccess(row)
                } else {
                    emitter.onError(Exception("Exception while sign Up"))
                }
            } else {
                emitter.onError(Exception("Exception while sign Up"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<Long>() {

                override fun onSuccess(value: Long) {
                    signUpLiveData.value = value
                }

                override fun onError(error: Throwable?) {
                    signUpLiveData.value = -1L
                    Log.e(TAG, "onError: inside signUp : Error : ${error?.message}")
                }

            })
    }

    fun signIn(email: String, password: String) {
        Single.create<User?> { emitter ->
            val userDAO = AcropolisAssignmentApplication.getDatabseInstance()?.userDAO()
            val user = userDAO?.signIn(email, password)
            emitter.onSuccess(user)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleSubscriber<User?>() {
                override fun onSuccess(user: User?) {
                    signInLiveData.value = user
                }

                override fun onError(error: Throwable?) {
                    Log.e(TAG, "onError: inside signIn() : Error ${error?.message}")
                    signInLiveData.value = null
                }

            })
    }

    companion object {
        private const val TAG = "UserViewModel"
    }
}