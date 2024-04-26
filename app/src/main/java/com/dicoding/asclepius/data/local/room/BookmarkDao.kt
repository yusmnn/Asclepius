package com.dicoding.asclepius.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.BookmarkEntity

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark_data")
    fun getAllBookmarksData(): LiveData<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmark_data WHERE image = :name")
    suspend fun deleteBookmark(name: String)

    @Query("SELECT * FROM bookmark_data WHERE image = :name")
    fun getBookmarkData(name: String): LiveData<BookmarkEntity>
}