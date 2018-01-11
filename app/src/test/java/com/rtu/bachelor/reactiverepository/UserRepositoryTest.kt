package com.rtu.bachelor.reactiverepository

import com.nhaarman.mockito_kotlin.mock
import com.rtu.bachelor.reactiverepository.data.api.UserApi
import com.rtu.bachelor.reactiverepository.data.database.UserDao
import com.rtu.bachelor.reactiverepository.data.models.User
import com.rtu.bachelor.reactiverepository.data.repository.Resource
import com.rtu.bachelor.reactiverepository.data.repository.Status
import com.rtu.bachelor.reactiverepository.data.repository.UserRepository
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import java.util.*

class UserRepositoryTest {

    lateinit var userRepository: UserRepository
    lateinit var userApi: UserApi
    lateinit var userDao: UserDao

    @Before
    fun setup() {
        userApi = mock()
        userDao = mock()
        userRepository = UserRepository(userApi, userDao)
    }

    @Test
    fun test_emptyDb_noDataOnApi_returnsEmptyList() {
        `when`(userApi.getUsers()).thenReturn(Observable.just(emptyList()))
        `when`(userDao.getUsers()).thenReturn(Single.just(emptyList()))
        val testObserver = userRepository.getUsers()
                .test()
        testObserver.awaitTerminalEvent()
        testObserver.assertValue {
            it.isEmpty()
        }
    }

    @Test
    fun something() {
        val result = PublishSubject.create<Resource<List<User>>>()
        result.subscribe(object : Observer<Resource<List<User>>> {
            override fun onSubscribe(d: Disposable?) {
                println("onSubscribe")
            }

            override fun onNext(t: Resource<List<User>>?) {
                println("onNext: " + t.toString())
            }

            override fun onComplete() {

                println("onComplete")
            }

            override fun onError(e: Throwable?) {
                println("onError")
            }

        })
        val a = result.last(Resource(null, Status.LOADING))
        result.onComplete()
        result.onNext(Resource(null, Status.SUCCESS))
        val rez = a.blockingGet()
        println("Hello")
    }

    @Test
    fun something2(){

    }

    fun aRandomUser() = User("mail@test.com", Random().nextLong())
}