package com.sibsutis.chat.repositories

import android.app.Application
import com.sibsutis.chat.database.ChatDatabase
import com.sibsutis.chat.entities.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val application: Application
) {

    private val userDao by lazy {
        ChatDatabase.getDatabase(application).userDao()
    }

    val getAllUsers = userDao.fetchAllUsers()

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun deleteUserById(id: Long) {
        userDao.deleteUserById(id)
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }
}