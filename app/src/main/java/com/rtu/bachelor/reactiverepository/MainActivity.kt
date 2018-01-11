package com.rtu.bachelor.reactiverepository

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.rtu.bachelor.reactiverepository.databinding.MainActivityBinding
import com.rtu.bachelor.reactiverepository.viewmodels.UserListViewModel
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var btn: Button

    lateinit var userListViewModel: UserListViewModel
    lateinit var binding: MainActivityBinding

    companion object {
        var counter = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        getUsersBtn.setOnClickListener {
            onGetUsers()
        }

        addSubscriberBtn.setOnClickListener {
            onAddSubscriber()
        }

        btn = getUsersBtn

        userListViewModel = UserListViewModel(App.get(this).userRepository2)
        userListViewModel.user.subscribe {
            Timber.d("Users: $it")
            binding.user = it[Random().nextInt(it.size)]
        }
        userListViewModel.posts.subscribe {
            Timber.d("Count: $it")
        }
    }

    private fun onGetUsers() {
        Timber.d("=====Getting users=====")
        userListViewModel.setId(Random().nextInt(3).toLong())
    }

    private fun onAddSubscriber() {
        Timber.d("=====Adding new subscriber=====")
        userListViewModel.user.subscribe {
            Timber.d("[NEW] Users: $it")
        }
        userListViewModel.posts.subscribe {
            Timber.d("[NEW] Count: $it")
        }
    }

//        userListViewModel.setId(3)

//        App.get(this).userRepository2.getUsers()
//                .subscribe {
//                    when (it.status) {
//                        Status.LOADING -> {
//                            Timber.d("Loading, $it")
//                        }
//                        Status.SUCCESS -> {
//                            Timber.d("Success, $it")
//                        }
//                    }
//                }

    /*
    App.get(this).userRepository.getUsers().subscribe(object : Observer<List<User>> {
        override fun onSubscribe(d: Disposable) {
            Timber.d("onSubscribe")
        }

        override fun onNext(users: List<User>) {
            Timber.d("Received ${users.size} users")
            for (user in users) {
                Timber.d(user.toString())
            }
        }

        override fun onError(e: Throwable) {
            Timber.e(e,"Error occurred")
        }

        override fun onComplete() {
            Timber.d("onComplete")
        }

    })
    */
}

