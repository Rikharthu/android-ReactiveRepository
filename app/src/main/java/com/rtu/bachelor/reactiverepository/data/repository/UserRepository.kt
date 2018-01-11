package com.rtu.bachelor.reactiverepository.data.repository

import com.rtu.bachelor.reactiverepository.data.api.UserApi
import com.rtu.bachelor.reactiverepository.data.database.UserDao
import com.rtu.bachelor.reactiverepository.data.models.User
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class UserRepository(private val userApi: UserApi, private val userDao: UserDao) {

    fun getUsers(): Observable<List<User>> {
        return Observable.concatArray(
                getUsersFromDb(),
                getUsersFromApi())
    }

    private fun getUsersFromDb(): Observable<out List<User>> {
        return userDao.getUsers().filter { it.isNotEmpty() }
                .subscribeOn(Schedulers.io())
                .toObservable()
                .doOnNext {
                    Timber.d("Dispatching ${it.size} users from DB")
                }
    }

    private fun getUsersFromApi(): Observable<out List<User>> {
        return userApi.getUsers()
                .doOnNext {
                    Timber.d("Dispatching ${it.size} users from API")
                    storeUsersInDb(it)
                }
    }

    private fun storeUsersInDb(users: List<User>) {
        Observable.fromCallable { userDao.insertAll(users) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    Timber.d("Inserted ${users.size} users from API in DB...")
                }
    }
}