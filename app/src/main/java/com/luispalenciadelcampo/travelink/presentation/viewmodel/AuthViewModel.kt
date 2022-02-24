package com.luispalenciadelcampo.travelink.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.luispalenciadelcampo.travelink.domain.usecases.UseCase
import com.luispalenciadelcampo.travelink.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel(){

    val userLogInStatus = MutableLiveData<Resource<AuthResult>>()
    val userSignInStatus = MutableLiveData<Resource<AuthResult>>()
    val sendEmailRecoverPasswordStatus = MutableLiveData<Resource<Boolean>>()

    suspend fun logInUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val loginResult = useCase.LoginUseCase(email, password)
            userLogInStatus.postValue(loginResult)
        }
    }

    suspend fun logInUserByGoogle(idToken: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val loginResult = useCase.LoginGoogleUseCase(idToken)
            userLogInStatus.postValue(loginResult)
        }
    }

    suspend fun registerUser(email: String, password: String, name: String, lastname: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val loginResult = useCase.RegisterUserUseCase(email, password, name, lastname)
            userSignInStatus.postValue(loginResult)
        }
    }

    suspend fun sendEmailRecoverPassword(email: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val sendEmailResult = useCase.SendRecoverPasswordEmailUseCase(email)
            sendEmailRecoverPasswordStatus.postValue(sendEmailResult)
        }
    }
}