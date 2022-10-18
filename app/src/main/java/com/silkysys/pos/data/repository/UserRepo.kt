package com.silkysys.pos.data.repository

import com.silkysys.pos.data.model.auth.user.update.UpdateUserRequest
import com.silkysys.pos.data.model.business.update.BusinessRequest
import com.silkysys.pos.data.network.APIService
import com.silkysys.pos.data.network.SafeApiCall
import javax.inject.Inject

class UserRepo @Inject constructor(val apiService: APIService) : SafeApiCall {

    suspend fun getUserDetails() = safeApiCall {
        apiService.getUserLoggedIn()
    }

    suspend fun getBusinessDetails() = safeApiCall {
        apiService.getBusinessDetails()
    }

    suspend fun updateBusinessDetails(businessName: String, taxNumber: String) = safeApiCall {
        apiService.updateBusinessDetails(BusinessRequest(businessName, taxNumber))
    }

    suspend fun updateUserDetails(
        firstName: String,
        lastName: String,
        email: String,
        address: String
    ) =
        safeApiCall {
            apiService.updateUser(UpdateUserRequest(firstName, lastName, email, address))
        }
}