package com.silkysys.pos.ui.user

import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import com.silkysys.pos.R
import com.silkysys.pos.data.model.auth.user.get.Data
import com.silkysys.pos.data.model.auth.user.get.UserResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentUserBinding
import com.silkysys.pos.ui.auth.LoginActivity
import com.silkysys.pos.util.*
import com.silkysys.pos.util.dialog.alertError
import com.silkysys.pos.util.dialog.alertLogout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding>(FragmentUserBinding::inflate),
    View.OnClickListener {

    private lateinit var viewModel: AuthViewModel
    private lateinit var userInfo: Data


    override fun FragmentUserBinding.initialize() {
        viewModel = ViewModelProvider(this@UserFragment)[AuthViewModel::class.java]
        btnLogout.setOnClickListener(this@UserFragment)
        btnBusinessInfo.setOnClickListener(this@UserFragment)
        btnUserInfo.setOnClickListener(this@UserFragment)

        // Api request to get user details
        Coroutines.background {
            val response = viewModel.getUser()
            Coroutines.main { displayUser(response) }
        }
    }

    override fun onClick(item: View) {
        when (item.id) {
            R.id.btn_logout -> logout()
            R.id.btn_business_info -> {
                goTo(UserFragmentDirections.actionProfileFragmentToBusinessDetailsFragment())
            }
            R.id.btn_user_info -> {
                goTo(UserFragmentDirections.actionProfileFragmentToUserDetailsFragment())
            }
        }
    }

    private fun displayUser(response: Resource<UserResponse>) {
        if (response is Resource.Success) {
            response.value.data.apply {
                userInfo = this
                switchVisibility()
                val fullName = "$first_name $last_name"
                val twoLetterName = first_name?.substring(0, 1) + last_name?.substring(0, 1)
                binding.tvTwoLetter.text = twoLetterName
                binding.tvUserName.text = fullName
                binding.tvEmail.text = email
            }
        } else {
            binding.progressBar.visibility = INVISIBLE
            alertError {
                retryButton {
                    binding.progressBar.visibility = VISIBLE
                    Coroutines.background {
                        val user = viewModel.getUser()
                        Coroutines.main { displayUser(user) }
                    }
                }
            }.show()
            handleApiError(response as Resource.Failure)
        }
    }

    private fun logout() {
        alertLogout {
            logoutButton {
                bindingLogout.btnLogout.visibility = INVISIBLE
                bindingLogout.progressBar.visibility = VISIBLE
                Coroutines.background {
                    val response = viewModel.logout()
                    Coroutines.main {
                        dialog?.dismiss()
                        if (response is Resource.Success) {
                            viewModel.removeUser()
                            requireContext().startActivity(LoginActivity::class.java)
                        } else handleApiError(response as Resource.Failure)
                    }
                }
            }
            cancelButton()
        }.show()
    }

    private fun switchVisibility() {
        binding.apply {
            progressBar.visibility = INVISIBLE
            cardView.visibility = VISIBLE
            tvUserName.visibility = VISIBLE
            tvEmail.visibility = VISIBLE
        }
    }
}