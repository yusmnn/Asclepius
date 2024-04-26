package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.entity.BookmarkEntity
import com.dicoding.asclepius.data.repository.BookmarkRepository
import kotlinx.coroutines.launch

class ResultViewModel(private val bookmarkRepository: BookmarkRepository) : ViewModel() {
    fun fetchBookmarkData(image: String) = bookmarkRepository.getBookmarkData(image)

    fun insert(image: String, label: String, score: String) {
        viewModelScope.launch {
            val bookmarkEntity = BookmarkEntity(
                image = image, label = label, score = score
            )
            bookmarkRepository.insert(bookmarkEntity)
        }
    }

    fun delete(image: String) {
        viewModelScope.launch {
            val imageName = image
            bookmarkRepository.delete(imageName)
        }
    }
}