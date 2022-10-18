package com.silkysys.pos.ui.auth

import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.local.UserPreferences
import com.silkysys.pos.data.model.auth.login.LoginResponse
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.ActivityLoginBinding
import com.silkysys.pos.ui.main.MainActivity
import com.silkysys.pos.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var userPreferences: UserPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.apply {
            tvCreateAccount.paintFlags = tvCreateAccount.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            tvForgotPassword.paintFlags = tvForgotPassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            btnLogin.setOnClickListener(this@LoginActivity)
            tvCreateAccount.setOnClickListener(this@LoginActivity)
            tvForgotPassword.setOnClickListener(this@LoginActivity)

            // Set gravity to end for "Password" in arabic language only
            val locale = Locale.getDefault().language
            if (locale == Constants.LANGUAGE_AR) etPassword.gravity = Gravity.END
            else etPassword.gravity = Gravity.START
        }
    }

    override fun onClick(item: View) {
        if (item.id == R.id.btn_login) login()
        else if (item.id == R.id.tv_create_account) openUrl(Constants.CREATE_ACCOUNT)
        else startActivity(ForgotPasswordActivity::class.java)
    }


    private fun login() {
        val username = binding.etUserName.text.toString()
        val password = binding.etPassword.text.toString()
        // Call login request only if user input are valid
        if (viewModel.checkUserInput(username, password, binding)) {
            setVisibility(Constants.LOADING)
            Coroutines.background {
                val response = viewModel.login(username, password)
                // Observe on api response
                withContext(Dispatchers.Main) {
                    if (response is Resource.Success) updateUi(response)
                    else {
                        setVisibility(Constants.FINISHED)
                        handleApiError(response as Resource.Failure)
                    }
                }

            }
        }
    }


    private fun updateUi(response: Resource.Success<LoginResponse>) {
        response.value.access_token?.let { viewModel.saveUser(it) }
        Constants.apply {
            if (binding.checkBox.isChecked) {
                userPreferences.write(REMEMBER_ME, CHECKED)
            }
        }
        startActivity(MainActivity::class.java)
        finish()
    }


    private fun setVisibility(status: String) {
        binding.apply {
            if (status == Constants.LOADING) {
                progressBar.visibility = VISIBLE
                btnLogin.visibility = INVISIBLE
            } else {
                progressBar.visibility = INVISIBLE
                btnLogin.visibility = VISIBLE
            }
        }
    }
}