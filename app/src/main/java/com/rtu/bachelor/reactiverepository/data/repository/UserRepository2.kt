package com.rtu.bachelor.reactiverepository.data.repository

import com.rtu.bachelor.reactiverepository.data.api.UserApi
import com.rtu.bachelor.reactiverepository.data.database.UserDao
import com.rtu.bachelor.reactiverepository.data.models.User
import com.rtu.bachelor.reactiverepository.data.preferences.PreferenceHelper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class UserRepository2(private val userApi: UserApi,
                      private val userDao: UserDao,
                      private val prefsHelper: PreferenceHelper) {

    val result = PublishSubject.create<Resource<List<User>>>()

    fun getUsers(forceRefresh: Boolean = false): Observable<Resource<List<User>>> {
        return getUsersFromDb()
                .flatMap {
                    if (forceRefresh || shouldFetch(it)) {
                        // If data needs to be fetched, then first return result from the database
                        // and then refresh it from the internet
                        Observable.concatArray(
                                Observable.just(it).flatMap {
                                    Observable.just(Resource(it, Status.LOADING))
                                },
                                getUsersFromApi().flatMap {
                                    Observable.just(Resource(it, Status.SUCCESS))
                                }
                        )
                    } else {
                        // Data is fresh enough, return it
                        Observable.just(it)
                                .flatMap {
                                    Observable.just(Resource(it, Status.SUCCESS))
                                }
                    }
                }
    }

    private fun setValue(value: Resource<List<User>>) {
        result.last(null).blockingGet()
    }

    private fun shouldFetch(it: List<User>?): Boolean {
        val lastRefreshedAt = prefsHelper.getLastRefreshedAt("users")
        val timeDiff = System.currentTimeMillis() - lastRefreshedAt
        val result = it == null || it.isEmpty() || timeDiff >= 5 * 60 * 1000 // 5 min
        Timber.d("shouldFetch: $result")
        return result
    }

    private fun getUsersFromDb(): Observable<List<User>> {
        Timber.d("Getting users from Database")
        return userDao.getUsers()
                .toObservable()
//                .filter { it.isNotEmpty() }
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    Timber.d("Dispatching ${it.size} users from DB")
                }
    }

    private fun getUsersFromApi(): Observable<List<User>> {
        Timber.d("Getting users from API")
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
                    prefsHelper.updateLastRefreshedAt("users")
                    Timber.d("Inserted ${users.size} users from API in DB...")
                }
    }
}