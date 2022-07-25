package com.sibsutis.chat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sibsutis.chat.dao.UserDao
import com.sibsutis.chat.entities.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        private lateinit var instance: ChatDatabase

        fun getDatabase(context: Context): ChatDatabase {
            if (!this::instance.isInitialized) {
                val builder = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = ChatDatabase::class.java,
                    name = "chat_database"
                )
                instance = builder.build()
            }

            return instance
        }
    }

}