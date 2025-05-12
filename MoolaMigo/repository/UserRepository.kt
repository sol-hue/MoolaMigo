package com.solidad.moolamigo.repository

import com.solidad.moolamigo.data.UserDao
import com.solidad.moolamigo.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }
}