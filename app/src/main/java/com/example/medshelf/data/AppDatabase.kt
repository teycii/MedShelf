package com.example.medshelf.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medshelf.model.DocumentEntity
import com.example.medshelf.model.FamilyMemberEntity
import com.example.medshelf.model.NoteEntity
import com.example.medshelf.model.ReminderEntity
import com.example.medshelf.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        DocumentEntity::class,
        NoteEntity::class,
        ReminderEntity::class,
        FamilyMemberEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun documentDao(): DocumentDao

    abstract fun noteDao(): NoteDao

    abstract fun reminderDao(): ReminderDao

    abstract fun familyMemberDao(): FamilyMemberDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medshelf_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { database ->
                        INSTANCE = database
                    }
            }
        }
    }
}