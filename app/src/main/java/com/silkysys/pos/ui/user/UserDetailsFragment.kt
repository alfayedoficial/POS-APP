package com.silkysys.pos.ui.user

import android.text.method.DigitsKeyListener
import android.view.Gravity.END
import android.view.Gravity.START
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.model.auth.user.get.UserResponse
import com.silkysys.pos.data.model.auth.user.update.UpdateUserResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.FragmentUserDetailsBinding
import com.silkysys.pos.util.*
import com.silkysys.pos.util.dialog.alertError
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UserDetailsFragment :
    BaseFragment<FragmentUserDetailsBinding>(FragmentUserDetailsBinding::inflate),
    View.OnClickListener {

    lateinit var viewModel: UserDetailsViewModel

    override fun FragmentUserDetailsBinding.initialize() {
        viewModel = ViewModelProvider(this@UserDetailsFragment)[UserDetailsViewModel::class.java]
        btnUpdate.setOnClickListener(this@UserDetailsFragment)
        viewModel.apply {
            initUserDetails()
            userDetailsResponse.observe(this@UserDetailsFragment) { updateUi(it) }
            userUpdateResponse.observe(this@UserDetailsFragment) { updateUser(it) }
        }
        setupViewsInRtl()
    }


    override fun onClick(item: View) {
        if (item.id == R.id.btn_update) {
            // Validate user input at first
            binding.apply {
                requireContext().apply {
                    val firstName = etFirstName.text.toString()
                    val lastName = etLastName.text.toString()
                    val address = etAddress.text.toString()
                    val email = etEmail.text.toString()

                    if (firstName.isEmpty()) {
                        etFirstName.error = displayError()
                    } else if (lastName.isEmpty()) {
                        etLastName.error = displayError()
                    }
                    // Create update business request
                    else {
                        btnUpdate.visibility = INVISIBLE
                        progressBarUpdate.visibility = VISIBLE
                        viewModel.initUpdateUser(firstName, lastName, email, address)
                    }
                }
            }
        }
    }


    private fun updateUi(response: Resource<UserResponse>) {
        binding.progressBar.visibility = INVISIBLE
        if (response is Resource.Success) {
            // Update views
            binding.apply {
                group.visibility = VISIBLE
                response.value.data.apply {
                    etFirstName.text = setText(first_name)
                    etLastName.text = setText(last_name)
                    etPhoneNumber.text = setText(locations[0].mobile)
                    etAddress.text = setText(address)
                    etEmail.text = setText(email)
                }
            }
        } else {
            handleApiError(response as Resource.Failure)
            alertError {
                retryButton {
                    viewModel.initUserDetails()
                    binding.progressBar.visibility = VISIBLE
                }
            }.show()
        }
    }


    private fun updateUser(response: Resource<UpdateUserResponse>) {
        binding.btnUpdate.visibility = VISIBLE
        binding.progressBarUpdate.visibility = INVISIBLE
        if (response is Resource.Success) {
            requireContext().toast(response.value.msg)
        } else {
            requireContext().handleApiError(response as Resource.Failure)
        }
    }


    private fun setupViewsInRtl() {
        binding.apply {
            Locale.getDefault().language.also { locale ->
                if (locale == Constants.LANGUAGE_AR) {
                    etFirstName.gravity = START
                    etLastName.gravity = START
                    etAddress.gravity = START
                    etEmail.gravity = END

                    // Restrict inputs of edittext to arabic
                    etFirstName.keyListener =
                        DigitsKeyListener.getInstance(getString(R.string.arabic_only))
                    etLastName.keyListener =
                        DigitsKeyListener.getInstance(getString(R.string.arabic_only))
                    etAddress.keyListener =
                        DigitsKeyListener.getInstance(getString(R.string.arabic_only))

                } else {
                    // Restrict inputs of edittext to english
                    etFirstName.keyListener =
                        DigitsKeyListener.getInstance(getString(R.string.english_only))
                    etLastName.keyListener =
                        DigitsKeyListener.getInstance(getString(R.string.english_only))
                    etAddress.keyListener =
                        DigitsKeyListener.getInstance(getString(R.string.english_only))
                }
            }
        }
    }
}