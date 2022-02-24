package com.luispalenciadelcampo.travelink.domain.usecases.auth

import com.google.firebase.auth.AuthResult
import com.luispalenciadelcampo.travelink.domain.repository.AuthRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class LoginGoogleUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(idToken: String): Resource<AuthResult> = authRepository.logInUserByGoogle(idToken)
}