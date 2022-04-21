package com.luispalenciadelcampo.travelink.presentation.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.databinding.ActivityRecoverPasswordBinding
import com.luispalenciadelcampo.travelink.presentation.viewmodel.AuthViewModel
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.luispalenciadelcampo.travelink.utils.PatternsAccount
import dagger.hilt.android.AndroidEntryPoint
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecoverPasswordBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setButtons()
        setObserver()

        binding.loadingAnimation.isVisible = false
    }

    private fun setButtons(){
        binding.btnSendEmail.setOnClickListener {
            //Check internet connection
            if(GenericFunctions.isDeviceConnectedToInternet(this)) {
                val email = binding.editTextEmail.text.toString()

                // Check if email is valid
                if (!PatternsAccount.isValidEmail(email)) {
                    binding.editTextEmail.error = getString(R.string.incorrect_email)
                    return@setOnClickListener
                }
                binding.editTextEmail.error = null

                // Send recover password email
                lifecycleScope.launch {
                    authViewModel.sendEmailRecoverPassword(email)
                }
            }
        }

        binding.textViewGoBackLogin.setOnClickListener {
            finish()
        }
    }

    private fun setObserver(){
        authViewModel.sendEmailRecoverPasswordStatus.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.loadingAnimation.isVisible = false
                    Snackbar.make(binding.constraintLayout, getString(R.string.success_email_recover_password), Snackbar.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    binding.loadingAnimation.isVisible = false

                    var errorMessage = ""
                    errorMessage = when (result.message) {
                        Constants.RESULT_SEND_EMAIL_RECOVER_PASSWORD_ERROR_ACCOUNT_NOT_EXISTING -> {
                            getString(R.string.error_not_existing_email)
                        }
                        else -> {
                            getString(R.string.error_email_recover_password)
                        }
                    }
                    Snackbar.make(binding.constraintLayout, errorMessage, Snackbar.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    binding.loadingAnimation.setTextViewVisibility(true)
                    binding.loadingAnimation.setTextStyle(true)
                    binding.loadingAnimation.setTextSize(12F)
                    binding.loadingAnimation.setTextMsg("Getting place photo")
                    binding.loadingAnimation.setEnlarge(5)
                    binding.loadingAnimation.isVisible = true
                }
            }
        }
    }
}