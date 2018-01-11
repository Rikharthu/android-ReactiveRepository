package com.rtu.bachelor.reactiverepository.data.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey
        @ColumnInfo(name = "login")
        val login: String,
        @ColumnInfo(name = "id")
        val id: Long)