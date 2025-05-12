package com.solidad.moolamigo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.solidad.moolamigo.model.Content

@Database(entities = [Content::class], version = 1, exportSchema = false)
abstract class ContentDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao

    companion object {
        @Volatile private var INSTANCE: ContentDatabase? = null

        fun getDatabase(context: Context): ContentDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ContentDatabase::class.java,
                    "content_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}