package com.luispalenciadelcampo.travelink.presentation.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.databinding.ActivityLoginBinding
import com.luispalenciadelcampo.travelink.utils.PatternsAccount
import com.luispalenciadelcampo.travelink.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.luispalenciadelcampo.travelink.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    private lateinit var binding: ActivityLoginBinding

    private lateinit var authViewModel: AuthViewModel

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        getGoogleSignIn()

        setButtons()
        setObserver()

        binding.loadingAnimation.isVisible = false

    }

    private fun getGoogleSignIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default_web_client_id)) // THE RESOURCE IS NOT WORKING
            .requestIdToken(Constants.DEFAULT_WEB_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setObserver(){
        authViewModel.userLogInStatus.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.loadingAnimation.isVisible = false

                    binding.textViewErrorLogin.visibility = View.GONE
                    userLoggedIn()
                }
                is Resource.Error -> {
                    binding.loadingAnimation.isVisible = false

                    binding.textViewErrorLogin.visibility = View.VISIBLE
                    var errorMessage = ""
                    errorMessage = when (result.message) {
                        Constants.RESULT_LOGIN_ERROR_WRONG_CREDENTIALS -> {
                            getString(R.string.wrong_credentials)
                        }
                        Constants.RESULT_LOGIN_ERROR_EMAIL_NOT_VERIFIED -> {
                            getString(R.string.error_email_verified)
                        }
                        Constants.RESULT_LOGIN_GOOGLE_ERROR -> {
                            getString(R.string.error_email_verified)
                        }
                        else -> {
                            getString(R.string.error_login)
                        }
                    }
                    binding.textViewErrorLogin.text = errorMessage

                }
                is Resource.Loading -> {
                    binding.loadingAnimation.setTextViewVisibility(true)
                    binding.loadingAnimation.setTextStyle(true)
                    binding.loadingAnimation.setTextSize(12F)
                    binding.loadingAnimation.setTextMsg("Getting place photo")
                    binding.loadingAnimation.setEnlarge(5)
                    binding.loadingAnimation.isVisible = true

                    binding.textViewErrorLogin.visibility = View.GONE
                    Log.d(TAG, "LOADING")
                }
            }
        }
    }

    private fun setButtons(){
        binding.btnLogin.setOnClickListener {
            val email = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            // Check if the email and passwords are valid
            if(!PatternsAccount.isValidEmail(email)){ // Email is not valid
                // Set the error in the EditText
                binding.editTextUsername.error = getString(R.string.incorrect_email)
                return@setOnClickListener
            } else if(!PatternsAccount.isValidPassword(password)){ // Password is not valid
                // Set the error in the EditText
                binding.editTextPassword.error = getString(R.string.incorrect_password)
                return@setOnClickListener
            }

            // Check the checkbox in order to remember user or not
            checkRememberMe()

            // Email and password are correct
            binding.editTextUsername.error = null
            binding.editTextPassword.error = null

            // Try to log in by email in Firebase
            lifecycleScope.launch {
                authViewModel.logInUser(email, password)
            }
        }

        // Sets a click listener in the login by Google button
        binding.btnSignInGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // Sets a click listener in the Create account textview
        binding.textViewSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity()::class.java))
            //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Sets a click listener in the forgot password textview
        binding.textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity()::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun checkRememberMe(){
        val sharedPref = this.getSharedPreferences(Constants.SHARED_PREFERENCES_APP, Context.MODE_PRIVATE) ?: return
        val edit = sharedPref.edit()
        edit.putBoolean(Constants.SHARED_PREFERENCE_REMEMBER_ME, binding.checkBoxRememberMe.isChecked)
        edit.apply()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncherSignInGoogle.launch(signInIntent)
    }

    private var resultLauncherSignInGoogle = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // There are no request codes
        val data: Intent? = result.data

        Log.d(TAG, "Result code: ${result.resultCode}")

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)

            //userViewModel.insertNewUser(account.id, account.displayName)
            firebaseAuthWithGoogle(account.idToken!!)

        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            Snackbar.make(binding.scrollView, getString(R.string.error_login_google), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        lifecycleScope.launch {
            authViewModel.logInUserByGoogle(idToken)
        }

        /*
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    if(task.getResult().additionalUserInfo?.isNewUser == true){
                        val name: String
                        if(firebaseAuth.currentUser!!.displayName != null){
                            name = firebaseAuth.currentUser!!.displayName.toString()
                        }else{
                            name = ""
                        }

                        userViewModel.insertNewUser(firebaseAuth.currentUser!!.uid, name)
                    }

                    userLoggedIn()

                } else {
                    // If sign in fails, display a message to the user.
                    Snackbar.make(binding.scrollView, getString(R.string.error_login_google), Snackbar.LENGTH_LONG).show()
                }
            }

         */
    }

    // Method that start MainActivity
    private fun userLoggedIn(){
        val intent = Intent(this, MainActivity().javaClass)
        intent.putExtra(Constants.LOGGED_BEFORE_MAIN_ACTIVITY, true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}