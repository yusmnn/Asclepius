package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.local.room.BookmarkDatabase
import com.dicoding.asclepius.data.repository.BookmarkRepository

object Injection {
    fun provideRepository(context: Context): BookmarkRepository {
        val database = BookmarkDatabase.getInstance(context)
        val dao = database.bookmarkDao()
        return BookmarkRepository.getInstance(dao)
    }
}