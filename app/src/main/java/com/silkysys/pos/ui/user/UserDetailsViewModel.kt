package com.silkysys.pos.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silkysys.pos.data.model.auth.user.get.UserResponse
import com.silkysys.pos.data.model.auth.user.update.UpdateUserResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.data.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(val repo: UserRepo) : ViewModel() {

    val userDetailsResponse = MutableLiveData<Resource<UserResponse>>()
    val userUpdateResponse = MutableLiveData<Resource<UpdateUserResponse>>()

    fun initUserDetails() {
        viewModelScope.launch {
            userDetailsResponse.value = repo.getUserDetails()
        }
    }

    fun initUpdateUser(
        firstName: String,
        lastName: String,
        email: String,
        address: String
    ) {
        viewModelScope.launch {
            userUpdateResponse.value = repo.updateUserDetails(firstName, lastName, email, address)
        }
    }
}