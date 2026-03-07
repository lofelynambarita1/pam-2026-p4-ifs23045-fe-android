package com.example.drakorapp.data.repository

import com.example.drakorapp.data.model.Drakor
import com.example.drakorapp.data.network.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class DrakorRepository {
    private val api get() = ApiClient.getService()
    private fun String.asText() = toRequestBody("text/plain".toMediaTypeOrNull())

    suspend fun getAllDrakors(search: String, genre: String, status: String): Result<List<Drakor>> =
        try {
            val r = api.getAllDrakors(search, genre, status)
            if (r.isSuccessful) Result.Success(r.body()?.data?.drakors ?: emptyList())
            else Result.Error("Gagal memuat: ${r.code()}")
        } catch (e: Exception) { Result.Error("Koneksi gagal: ${e.message}") }

    suspend fun getDrakorById(id: String): Result<Drakor> =
        try {
            val r = api.getDrakorById(id)
            if (r.isSuccessful) Result.Success(r.body()?.data?.drakor ?: Drakor())
            else Result.Error("Tidak ditemukan: ${r.code()}")
        } catch (e: Exception) { Result.Error("Koneksi gagal: ${e.message}") }

    suspend fun createDrakor(
        judul: String, genre: String, tahun: Int, episode: Int,
        rating: Double, sinopsis: String, status: String, file: File
    ): Result<String> =
        try {
            val poster = MultipartBody.Part.createFormData(
                "poster", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
            )
            val r = api.createDrakor(
                judul.asText(), genre.asText(), tahun.toString().asText(),
                episode.toString().asText(), rating.toString().asText(),
                sinopsis.asText(), status.asText(), poster
            )
            if (r.isSuccessful) Result.Success(r.body()?.data?.drakorId ?: "")
            else Result.Error("Gagal tambah: ${r.code()}")
        } catch (e: Exception) { Result.Error("Koneksi gagal: ${e.message}") }

    suspend fun updateDrakor(
        id: String, judul: String, genre: String, tahun: Int, episode: Int,
        rating: Double, sinopsis: String, status: String, file: File?
    ): Result<Unit> =
        try {
            val r = if (file != null) {
                val poster = MultipartBody.Part.createFormData(
                    "poster", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
                )
                api.updateDrakorDenganPoster(
                    id, judul.asText(), genre.asText(), tahun.toString().asText(),
                    episode.toString().asText(), rating.toString().asText(),
                    sinopsis.asText(), status.asText(), poster
                )
            } else {
                api.updateDrakorTanpaPoster(
                    id, judul.asText(), genre.asText(), tahun.toString().asText(),
                    episode.toString().asText(), rating.toString().asText(),
                    sinopsis.asText(), status.asText()
                )
            }
            if (r.isSuccessful) Result.Success(Unit)
            else Result.Error("Gagal update: ${r.code()}")
        } catch (e: Exception) { Result.Error("Koneksi gagal: ${e.message}") }

    suspend fun deleteDrakor(id: String): Result<Unit> =
        try {
            val r = api.deleteDrakor(id)
            if (r.isSuccessful) Result.Success(Unit)
            else Result.Error("Gagal hapus: ${r.code()}")
        } catch (e: Exception) { Result.Error("Koneksi gagal: ${e.message}") }

    suspend fun getProfile(): Result<Map<String, String>> =
        try {
            val r = api.getProfile()
            if (r.isSuccessful) Result.Success(r.body()?.data ?: emptyMap())
            else Result.Error("Gagal muat profil")
        } catch (e: Exception) { Result.Error("Koneksi gagal: ${e.message}") }
}
