package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LikedTrackEntity::class,
        LocalPlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        RecentSearchEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VyboraDatabase : RoomDatabase() {
    abstract fun dao(): VyboraDao

    companion object {
        @Volatile
        private var instance: VyboraDatabase? = null

        fun getInstance(context: Context): VyboraDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    VyboraDatabase::class.java,
                    "vybora_music.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
            }
        }
    }
}
