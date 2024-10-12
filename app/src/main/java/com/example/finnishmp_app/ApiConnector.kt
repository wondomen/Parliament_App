package com.example.finnishmp_app

import com.example.finnishmp_app.db.ParliamentMember
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/*
Muche Berhanu 2219580

ApiConnector is a singleton object that manages the configuration and creation of the Retrofit
instance used to connect to a RESTful API for fetching data related to parliament members.
*/
object ApiConnector {
    private const val BASE_URL = "https://users.metropolia.fi/~berhanud/"

    private val retrofit: Retrofit by lazy {
        createRetrofit()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

interface ApiService {
    @GET("seating.json")
    fun loadMainData(): Call<List<ParliamentMember>>

    @GET("extras.json")
    fun loadExtraData(): Call<List<ParliamentMember>>
}
