package com.rtu.bachelor.reactiverepository.viewmodels

import com.rtu.bachelor.reactiverepository.data.models.User
import com.rtu.bachelor.reactiverepository.data.repository.UserRepository2
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*

class UserListViewModel(private val repository: UserRepository2) {

    private val userId = PublishSubject.create<Long>()
    val user = PublishSubject.create<List<User>>()!!
    val posts = PublishSubject.create<String>()!!

    init {
        userId.subscribe {
            Timber.d("User ID changed to: $it")
            updateUsers()
            updatePosts()
        }

        // TODO how to avoid it making calls for each subscriber
//        user = userId
//                .distinctUntilChanged()
//                .flatMap {
//                    repository.getUsers()
//                            .flatMap {
//                                Observable.just(it.data)
//                            }
//                }
//        posts = userId
//                .distinctUntilChanged()
//                .flatMap {
//                    Observable.just("Posts for user: ${Random().nextInt()}")
//                }
    }

    private fun updatePosts() {
        posts.onNext("Posts for user: ${Random().nextInt()}")
    }

    private fun updateUsers() {
        repository.getUsers()
                .subscribe {
                    Timber.d("Updating users")
                    user.onNext(it.data)
                }
    }

    fun setId(id: Long) {
        userId.onNext(id)
    }
}