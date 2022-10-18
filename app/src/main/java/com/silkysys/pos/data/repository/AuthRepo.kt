package com.silkysys.pos.data.repository

import com.silkysys.pos.data.local.CartDao
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.local.UserPreferences
import com.silkysys.pos.data.model.auth.forget.ForgetPasswordRequest
import com.silkysys.pos.data.model.auth.login.LoginRequest
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.SafeApiCall
import com.silkysys.pos.util.Coroutines
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val apiService: APIService,
    val cartDao: CartDao,
    private val userPreferences: UserPreferences
) : SafeApiCall {


    suspend fun loginUser(username: String, password: String) = safeApiCall {
        apiService.login(LoginRequest(username, password))
    }

    suspend fun logoutUser() = safeApiCall {
        apiService.logout()
    }

    suspend fun getUser() = safeApiCall {
        apiService.getUserLoggedIn()
    }

    suspend fun forgotPassword(email: String) = safeApiCall {
        apiService.forgetPassword(ForgetPasswordRequest(email))
    }

    fun saveUser(token: String) {
        userPreferences.write(Constants.USER_TOKEN, token)
    }

    fun removeUser() {
        userPreferences.apply {
            Constants.apply {
                remove(REMEMBER_ME)
                remove(USER_TOKEN)
                remove(CART_BADGE)
            }
        }
        Coroutines.background {
            cartDao.deleteAll()
        }
    }
}