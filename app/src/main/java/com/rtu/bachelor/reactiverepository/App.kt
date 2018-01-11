package com.rtu.bachelor.reactiverepository

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.rtu.bachelor.reactiverepository.data.api.UserApi
import com.rtu.bachelor.reactiverepository.data.database.AppDatabase
import com.rtu.bachelor.reactiverepository.data.preferences.PreferenceHelper
import com.rtu.bachelor.reactiverepository.data.repository.UserRepository
import com.rtu.bachelor.reactiverepository.data.repository.UserRepository2
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class App : Application() {

    lateinit var appDatabase: AppDatabase
        private set
    lateinit var userApi: UserApi
        private set
    lateinit var userRepository: UserRepository
        private set
    lateinit var userRepository2: UserRepository2
        private set
    lateinit var preferenceHelper: PreferenceHelper
        private set

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        appDatabase = Room.databaseBuilder(this,
                AppDatabase::class.java, "mvvm-database").build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        userApi = retrofit.create(UserApi::class.java)

        userRepository = UserRepository(userApi, appDatabase.userDao())
        preferenceHelper = PreferenceHelper(this)
        userRepository2 = UserRepository2(userApi, appDatabase.userDao(), preferenceHelper)
    }

    companion object {
        fun get(context: Context) = context.applicationContext as App
    }
}