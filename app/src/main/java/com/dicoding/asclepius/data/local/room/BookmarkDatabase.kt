package com.dicoding.asclepius.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.local.entity.BookmarkEntity

@Database(entities = [BookmarkEntity::class], version = 1)
abstract class BookmarkDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var instance: BookmarkDatabase? = null
        fun getInstance(context: Context): BookmarkDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                // change name db
                context.applicationContext, BookmarkDatabase::class.java, "Bookmark.db"
            ).fallbackToDestructiveMigration().build()
        }
    }
}