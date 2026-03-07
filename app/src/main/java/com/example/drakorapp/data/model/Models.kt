package com.example.drakorapp.data.model

data class Drakor(
    val id: String = "",
    val judul: String = "",
    val pathPoster: String = "",
    val genre: String = "",
    val tahun: Int = 0,
    val episode: Int = 0,
    val rating: Double = 0.0,
    val sinopsis: String = "",
    val status: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class DataResponse<T>(
    val status: String = "",
    val message: String = "",
    val data: T? = null
)

data class DrakorListData(val drakors: List<Drakor> = emptyList())
data class DrakorDetailData(val drakor: Drakor = Drakor())
data class DrakorIdData(val drakorId: String = "")
