package com.dicoding.mystoryapp.api_settings

import com.dicoding.mystoryapp.api_settings.responses.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("/v1/login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("/v1/register")
    fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<RegisterResponse>

    @GET("/v1/stories")
    fun getStories(
        @Header("Authorization") authToken: String
    ): Call<StoriesResponse>

    @GET("/v1/stories?location=1")
    fun getMapStories(
        @Header("Authorization") authToken: String
    ): Call<StoriesResponse>

    @GET("stories")
    suspend fun getStoriesPaging(
        @Header("Authorization") authToken: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): StoriesResponse

    @GET("/v1/stories/{id}")
    fun getDetailStory(
        @Path("id") id: String,
        @Header("Authorization") authToken: String
    ): Call<StoriesDetailResponse>

    @Multipart
    @POST("/v1/stories")
    fun addStory(
        @Header("Authorization") authToken: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Call<AddStoryResponse>
}