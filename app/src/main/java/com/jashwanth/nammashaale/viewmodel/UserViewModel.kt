package com.jashwanth.nammashaale.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jashwanth.nammashaale.data.User
import com.jashwanth.nammashaale.data.UserRepository
import com.jashwanth.nammashaale.data.UserRole
import com.jashwanth.nammashaale.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun login(username: String, password: String, onSuccess: (UserRole) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(username, password)
            if (user != null) {
                _currentUser.value = user
                _error.value = null
                onSuccess(user.role)
            } else {
                _error.value = "Invalid username or password"
            }
        }
    }

    fun signup(username: String, password: String, role: UserRole, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val existingUser = repository.getUserByUsername(username)
            if (existingUser == null) {
                val newUser = User(username = username, password = password, role = role)
                repository.signup(newUser)
                _error.value = null
                onSuccess()
            } else {
                _error.value = "Username already exists"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
