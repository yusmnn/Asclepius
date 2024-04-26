package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.repository.BookmarkRepository
import kotlinx.coroutines.launch

class BookmarkViewModel(private val bookmarkRepository: BookmarkRepository) : ViewModel() {
    fun getAllBookmarksData() = bookmarkRepository.getAllBookmarksData()

    fun delete(image: String) {
        viewModelScope.launch {
            val imageName = image
            bookmarkRepository.delete(imageName)
        }
    }
}