package com.example.customclipboard.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.customclipboard.data.ClipDatabase
import com.example.customclipboard.data.ClipEntity
import com.example.customclipboard.data.ClipRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClipboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ClipRepository(
        ClipDatabase.getDatabase(application).clipDao(),
        application
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val clipsState: StateFlow<List<ClipEntity>> = _searchQuery
        .flatMapLatest { query -> repository.searchClips(query) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val clipCount: StateFlow<Int> = repository.clipCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun copyClipToSystem(text: String) {
        repository.copyToSystemClipboard(text)
    }

    fun deleteClip(clipId: Long) {
        viewModelScope.launch {
            repository.deleteClip(clipId)
        }
    }
}
