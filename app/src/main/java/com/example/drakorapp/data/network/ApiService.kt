package com.example.drakorapp.data.network

import com.example.drakorapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("drakors")
    suspend fun getAllDrakors(
        @Query("search") search: String = "",
        @Query("genre")  genre: String  = "",
        @Query("status") status: String = ""
    ): Response<DataResponse<DrakorListData>>

    @GET("drakors/{id}")
    suspend fun getDrakorById(@Path("id") id: String): Response<DataResponse<DrakorDetailData>>

    @Multipart
    @POST("drakors")
    suspend fun createDrakor(
        @Part("judul")    judul:    RequestBody,
        @Part("genre")    genre:    RequestBody,
        @Part("tahun")    tahun:    RequestBody,
        @Part("episode")  episode:  RequestBody,
        @Part("rating")   rating:   RequestBody,
        @Part("sinopsis") sinopsis: RequestBody,
        @Part("status")   status:   RequestBody,
        @Part            poster:   MultipartBody.Part
    ): Response<DataResponse<DrakorIdData>>

    @Multipart
    @PUT("drakors/{id}")
    suspend fun updateDrakorDenganPoster(
        @Path("id")       id:       String,
        @Part("judul")    judul:    RequestBody,
        @Part("genre")    genre:    RequestBody,
        @Part("tahun")    tahun:    RequestBody,
        @Part("episode")  episode:  RequestBody,
        @Part("rating")   rating:   RequestBody,
        @Part("sinopsis") sinopsis: RequestBody,
        @Part("status")   status:   RequestBody,
        @Part            poster:   MultipartBody.Part
    ): Response<DataResponse<Nothing>>

    @Multipart
    @PUT("drakors/{id}")
    suspend fun updateDrakorTanpaPoster(
        @Path("id")       id:       String,
        @Part("judul")    judul:    RequestBody,
        @Part("genre")    genre:    RequestBody,
        @Part("tahun")    tahun:    RequestBody,
        @Part("episode")  episode:  RequestBody,
        @Part("rating")   rating:   RequestBody,
        @Part("sinopsis") sinopsis: RequestBody,
        @Part("status")   status:   RequestBody
    ): Response<DataResponse<Nothing>>

    @DELETE("drakors/{id}")
    suspend fun deleteDrakor(@Path("id") id: String): Response<DataResponse<Nothing>>

    @GET("profile")
    suspend fun getProfile(): Response<DataResponse<Map<String, String>>>
}
