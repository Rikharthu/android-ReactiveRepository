package com.rtu.bachelor.reactiverepository.data.repository

data class Resource<T>(
        val data: T?,
        val status: Status)