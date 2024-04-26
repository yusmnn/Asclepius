package com.dicoding.asclepius.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.asclepius.data.local.entity.BookmarkEntity
import com.dicoding.asclepius.data.local.room.BookmarkDao
import com.dicoding.asclepius.util.Result

class BookmarkRepository private constructor(
    private val bookmarkDao: BookmarkDao,
) {

    fun getAllBookmarksData(): LiveData<Result<List<BookmarkEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val data = bookmarkDao.getAllBookmarksData()
            val localData: LiveData<Result<List<BookmarkEntity>>> = data.map { Result.Success(it) }
            emitSource(localData)

        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getBookmarkData(name: String): LiveData<BookmarkEntity> {
        return bookmarkDao.getBookmarkData(name)
    }

    suspend fun insert(bookmark: BookmarkEntity) {
        bookmarkDao.insertBookmark(bookmark)
    }

    suspend fun delete(name: String) {
        bookmarkDao.deleteBookmark(name)
    }

    companion object {
        @Volatile
        private var instance: BookmarkRepository? = null
        fun getInstance(
            bookmarkDao: BookmarkDao,
        ): BookmarkRepository =
            instance ?: synchronized(this) {
                instance ?: BookmarkRepository(bookmarkDao)
            }.also { instance = it }
    }

}