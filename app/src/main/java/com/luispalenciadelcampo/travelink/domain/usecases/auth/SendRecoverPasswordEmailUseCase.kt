package com.luispalenciadelcampo.travelink.domain.usecases.auth

import com.luispalenciadelcampo.travelink.domain.repository.AuthRepository
import javax.inject.Inject
import com.luispalenciadelcampo.travelink.utils.Resource

class SendRecoverPasswordEmailUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(email: String):
            Resource<Boolean> = authRepository.sendRecoverPasswordEmail(email)
}