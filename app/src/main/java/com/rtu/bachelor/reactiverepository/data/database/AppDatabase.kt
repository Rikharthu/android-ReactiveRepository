package com.rtu.bachelor.reactiverepository.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.rtu.bachelor.reactiverepository.data.models.User

@Database(entities = arrayOf(User::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}