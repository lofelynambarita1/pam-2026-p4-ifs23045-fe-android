package com.example.drakorapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drakorapp.data.model.Drakor
import com.example.drakorapp.data.network.ApiClient
import com.example.drakorapp.data.repository.DrakorRepository
import com.example.drakorapp.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class UiState<out T> {
    object Idle    : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class DrakorViewModel : ViewModel() {
    private val repo = DrakorRepository()

    private val _listState    = MutableStateFlow<UiState<List<Drakor>>>(UiState.Idle)
    val listState: StateFlow<UiState<List<Drakor>>> = _listState

    private val _detailState  = MutableStateFlow<UiState<Drakor>>(UiState.Idle)
    val detailState: StateFlow<UiState<Drakor>> = _detailState

    private val _actionState  = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState

    private val _profileState = MutableStateFlow<UiState<Map<String, String>>>(UiState.Idle)
    val profileState: StateFlow<UiState<Map<String, String>>> = _profileState

    val searchQuery  = MutableStateFlow("")
    val filterGenre  = MutableStateFlow("")
    val filterStatus = MutableStateFlow("")

    fun loadDrakors() = viewModelScope.launch {
        _listState.value = UiState.Loading
        _listState.value = when (val r = repo.getAllDrakors(
            searchQuery.value, filterGenre.value, filterStatus.value)) {
            is Result.Success -> UiState.Success(r.data)
            is Result.Error   -> UiState.Error(r.message)
        }
    }

    fun loadDrakorById(id: String) = viewModelScope.launch {
        _detailState.value = UiState.Loading
        _detailState.value = when (val r = repo.getDrakorById(id)) {
            is Result.Success -> UiState.Success(r.data)
            is Result.Error   -> UiState.Error(r.message)
        }
    }

    fun createDrakor(
        judul: String, genre: String, tahun: Int, episode: Int,
        rating: Double, sinopsis: String, status: String, file: File,
        onDone: () -> Unit
    ) = viewModelScope.launch {
        _actionState.value = UiState.Loading
        when (val r = repo.createDrakor(judul, genre, tahun, episode, rating, sinopsis, status, file)) {
            is Result.Success -> { _actionState.value = UiState.Success(Unit); loadDrakors(); onDone() }
            is Result.Error   -> _actionState.value = UiState.Error(r.message)
        }
    }

    fun updateDrakor(
        id: String, judul: String, genre: String, tahun: Int, episode: Int,
        rating: Double, sinopsis: String, status: String, file: File?,
        onDone: () -> Unit
    ) = viewModelScope.launch {
        _actionState.value = UiState.Loading
        when (val r = repo.updateDrakor(id, judul, genre, tahun, episode, rating, sinopsis, status, file)) {
            is Result.Success -> { _actionState.value = UiState.Success(Unit); loadDrakors(); onDone() }
            is Result.Error   -> _actionState.value = UiState.Error(r.message)
        }
    }

    fun deleteDrakor(id: String, onDone: () -> Unit) = viewModelScope.launch {
        _actionState.value = UiState.Loading
        when (val r = repo.deleteDrakor(id)) {
            is Result.Success -> { _actionState.value = UiState.Success(Unit); loadDrakors(); onDone() }
            is Result.Error   -> _actionState.value = UiState.Error(r.message)
        }
    }

    fun loadProfile() = viewModelScope.launch {
        _profileState.value = UiState.Loading
        _profileState.value = when (val r = repo.getProfile()) {
            is Result.Success -> UiState.Success(r.data)
            is Result.Error   -> UiState.Error(r.message)
        }
    }

    fun resetAction() { _actionState.value = UiState.Idle }
    fun setBaseUrl(url: String) { ApiClient.setBaseUrl(url) }
    fun getBaseUrl() = ApiClient.getBaseUrl()
}
