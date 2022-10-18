package com.silkysys.pos.util

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.silkysys.pos.R
import com.silkysys.pos.data.model.auth.forget.ForgotPasswordResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.data.repository.AuthRepo
import com.silkysys.pos.databinding.ActivityLoginBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepo,
    application: Application
) : AndroidViewModel(application) {

    val context = getApplication<Application>()
    val forgetPasswordResponse = MutableLiveData<Resource<ForgotPasswordResponse>>()

    fun checkUserInput(
        username: String,
        password: String,
        binding: ActivityLoginBinding
    ): Boolean {
        binding.apply {
            return when {
                username.isEmpty() -> {
                    etUserName.error = context.resources.getString(R.string.required_field)
                    false
                }
                password.isEmpty() -> {
                    etPassword.error = context.resources.getString(R.string.required_field)
                    false
                }
                else -> true
            }

        }
    }

    fun initForgotPassword(email: String) {
        viewModelScope.launch {
            forgetPasswordResponse.value = repo.forgotPassword(email)
        }
    }

    suspend fun login(username: String, password: String) = repo.loginUser(username, password)

    suspend fun logout() = repo.logoutUser()

    suspend fun getUser() = repo.getUser()

    fun saveUser(token: String) {
        repo.saveUser(token)
    }

    fun removeUser() {
        repo.removeUser()
    }
}