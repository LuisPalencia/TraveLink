package com.luispalenciadelcampo.travelink.domain.usecases.auth

import com.google.firebase.auth.AuthResult
import com.luispalenciadelcampo.travelink.domain.repository.AuthRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(email: String, password: String): Resource<AuthResult> = authRepository.logInUser(email, password)
}