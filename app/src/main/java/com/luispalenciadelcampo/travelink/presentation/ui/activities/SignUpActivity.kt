package com.luispalenciadelcampo.travelink.presentation.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.utils.Resource
import com.luispalenciadelcampo.travelink.databinding.ActivitySignUpBinding
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.luispalenciadelcampo.travelink.utils.PatternsAccount
import com.google.android.material.snackbar.Snackbar
import com.luispalenciadelcampo.travelink.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setButtons()
        setEditTextListeners()
        setObserver()
    }

    private fun setObserver(){
        authViewModel.userSignInStatus.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, getString(R.string.success_account_creation), Toast.LENGTH_LONG).show()
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "ERROR AL CREAR USUARIO", Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {

                }
            }
        }
    }

    private fun setButtons(){

        binding.btnSignUp.setOnClickListener {
            //Check internet connection
            if(GenericFunctions.isDeviceConnectedToInternet(this)){
                val firstname = binding.editTextFirstName.text.toString()
                val lastname = binding.editTextLastName.text.toString()
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                val confirmPassword = binding.editTextConfirmPassword.text.toString()

                // checks these three things: email valid, password that matches minimum requiriments, and the confirm password is the same as the password
                if(!PatternsAccount.isValidEmail(email) ||
                    firstname.isEmpty() ||
                    lastname.isEmpty() ||
                    !PatternsAccount.isValidPassword(password) ||
                    !PatternsAccount.isValidConfirmPassword(password, confirmPassword)
                ){ // There are errors in the data introduced
                    Snackbar.make(it, getString(R.string.error_in_data_account), Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }else if(!binding.checkBoxAgreePrivacyPolicy.isChecked) {
                    Snackbar.make(it, getString(R.string.error_sign_up_privacy_policy), Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // All data is correct, proceed to create user
                lifecycleScope.launch {
                    authViewModel.registerUser(email, password, firstname, lastname)
                }
            }else{
                // Show error
                GenericFunctions.getErrorInternetDialog(this).show()
            }
        }

        binding.textViewLogIn.setOnClickListener{
            finish()
        }
    }

    // Function that set up the funcionality of the Edittexts
    // All EditTexts will have a listener when a character is added in order to check if fields are correct
    private fun setEditTextListeners(){
        // Sets a text changed listener in the email edittext
        binding.editTextFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val firstName = binding.editTextFirstName.text.toString()
                if(firstName.isEmpty()){
                    // If there are some errors, the error message is enabled
                    //enableErrorMessage(getString(R.string.incorrect_email))
                    binding.editTextFirstName.error = getString(R.string.incorrect_name)
                }else{
                    // If everything is fine, disable error message
                    //disableErrorMessage()
                    binding.editTextFirstName.error = null
                }
            }
        })

        binding.editTextLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val lastname = binding.editTextLastName.text.toString()
                if(lastname.isEmpty()){
                    // If there are some errors, the error message is enabled
                    //enableErrorMessage(getString(R.string.incorrect_email))
                    binding.editTextLastName.error = getString(R.string.incorrect_last_name)
                }else{
                    // If everything is fine, disable error message
                    //disableErrorMessage()
                    binding.editTextLastName.error = null
                }
            }
        })

        // Sets a text changed listener in the name edittext
        binding.editTextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val email = binding.editTextEmail.text.toString()

                if(!PatternsAccount.isValidEmail(email)){
                    // If there are some errors, the error message is enabled
                    //enableErrorMessage(getString(R.string.incorrect_name))
                    binding.editTextEmail.error = getString(R.string.incorrect_email)
                }else{
                    // If everything is fine, disable error message
                    //disableErrorMessage()
                    binding.editTextEmail.error = null
                }
            }

        })

        // Sets a text changed listener in the password edittext
        binding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val password = binding.editTextPassword.text.toString()

                if(!PatternsAccount.isValidPassword(password)){
                    // If there are some errors, the error message is enabled
                    //enableErrorMessage(getString(R.string.incorrect_password))
                    binding.editTextPassword.error = getString(R.string.incorrect_password)
                }else{
                    // If everything is fine, disable error message
                    //disableErrorMessage()
                    binding.editTextPassword.error = null
                }
            }

        })

        // Sets a text changed listener in the confirm password edittext
        binding.editTextConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val password = binding.editTextPassword.text.toString()
                val confirmPassword = binding.editTextConfirmPassword.text.toString()
                if(!PatternsAccount.isValidConfirmPassword(password, confirmPassword)){
                    // If there are some errors, the error message is enabled
                    //enableErrorMessage(getString(R.string.different_passwords))
                    binding.editTextConfirmPassword.error = getString(R.string.different_passwords)
                }else{
                    // If everything is fine, disable error message
                    //disableErrorMessage()
                    binding.editTextConfirmPassword.error = null
                }
            }

        })
    }
}