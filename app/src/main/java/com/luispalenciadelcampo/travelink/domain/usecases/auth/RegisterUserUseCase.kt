package com.luispalenciadelcampo.travelink.domain.usecases.auth

import com.google.firebase.auth.AuthResult
import com.luispalenciadelcampo.travelink.domain.repository.AuthRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(email: String, password: String, name: String, lastName: String):
            Resource<AuthResult> = authRepository.registerUser(email, password, name, lastName)
}