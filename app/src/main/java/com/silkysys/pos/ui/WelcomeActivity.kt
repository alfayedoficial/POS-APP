package com.silkysys.pos.ui

import android.os.Bundle
import android.view.View.INVISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.silkysys.pos.R
import com.silkysys.pos.data.local.Constants
import com.silkysys.pos.data.local.UserPreferences
import com.silkysys.pos.data.network.Resource
import com.silkysys.pos.databinding.ActivityWelcomeBinding
import com.silkysys.pos.ui.auth.LoginActivity
import com.silkysys.pos.ui.main.MainActivity
import com.silkysys.pos.util.AuthViewModel
import com.silkysys.pos.util.Coroutines
import com.silkysys.pos.util.handleApiError
import com.silkysys.pos.util.startActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var userPreferences: UserPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_POS)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        // Check if user is logged in or not
        Coroutines.background {
            val response = viewModel.getUser()
            Coroutines.main {
                if (response is Resource.Success) {
                    if (userPreferences.read(Constants.REMEMBER_ME) == "") loginUser()
                    else {
                        startActivity(MainActivity::class.java)
                        finish()
                    }
                } else {
                    handleApiError(response as Resource.Failure)
                    binding.progressBar.visibility = INVISIBLE
                    viewModel.removeUser()
                    loginUser()
                }
            }
        }

    }


    private fun loginUser() {
        startActivity(LoginActivity::class.java)
        finish()
    }
}