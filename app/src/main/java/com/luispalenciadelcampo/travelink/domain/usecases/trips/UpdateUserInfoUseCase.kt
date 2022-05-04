package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.data.dto.User
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(user: User): Resource<User> = mainRepository.updateUserInfo(user)
}