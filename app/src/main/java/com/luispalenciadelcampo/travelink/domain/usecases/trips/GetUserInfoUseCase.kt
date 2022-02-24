package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.data.dto.User
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import javax.inject.Inject
import com.luispalenciadelcampo.travelink.utils.Resource

class GetUserInfoUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(userId: String): Resource<User> = mainRepository.getUser(userId)
}