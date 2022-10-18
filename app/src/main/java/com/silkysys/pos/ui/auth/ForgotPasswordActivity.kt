package com.silkysys.pos.ui.auth

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.silkysys.pos.R
import com.silkysys.pos.data.model.auth.forget.ForgotPasswordResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.ActivityForgotPasswordBinding
import com.silkysys.pos.util.AuthViewModel
import com.silkysys.pos.util.handleApiError
import com.silkysys.pos.util.startActivity
import com.silkysys.pos.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityForgotPasswordBinding
    lateinit var viewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Login)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        viewModel.forgetPasswordResponse.observe(this) { response ->
            confirmRestPassword(response)
        }
        binding.apply {
            tvSignIn.paintFlags = tvSignIn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            tvSignIn.setOnClickListener(this@ForgotPasswordActivity)
            btnResetPassword.setOnClickListener(this@ForgotPasswordActivity)
        }
    }


    override fun onClick(item: View) {
        if (item.id == R.id.tv_sign_in) startActivity(LoginActivity::class.java)
        else restPassword()
    }


    private fun restPassword() {
        binding.apply {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                etEmail.error = resources.getString(R.string.required_field)
            } else {
                btnResetPassword.visibility = INVISIBLE
                progressBar.visibility = VISIBLE
                viewModel.initForgotPassword(email)
            }
        }
    }


    private fun confirmRestPassword(response: Resource<ForgotPasswordResponse>) {
        binding.btnResetPassword.visibility = VISIBLE
        binding.progressBar.visibility = INVISIBLE
        if (response is Resource.Success) {
            if (response.value.status == 200) {
                toast(getString(R.string.forgot_password_confrim))
            } else toast(response.value.message)

        } else handleApiError(response as Resource.Failure)
    }
}