package com.rtu.bachelor.reactiverepository.data.api

import com.rtu.bachelor.reactiverepository.data.models.User
import io.reactivex.Observable
import retrofit2.http.GET

interface UserApi {

    @GET("/users")
    fun getUsers(): Observable<List<User>>
}