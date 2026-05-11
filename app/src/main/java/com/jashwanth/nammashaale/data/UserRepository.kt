package com.jashwanth.nammashaale.data

import com.jashwanth.nammashaale.database.UserDao

class UserRepository(private val userDao: UserDao) {
    suspend fun login(username: String, password: String): User? {
        return userDao.login(username, password)
    }

    suspend fun signup(user: User) {
        userDao.signup(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
}
