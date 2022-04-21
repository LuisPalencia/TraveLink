package com.luispalenciadelcampo.travelink.domain.usecases.trips

import android.graphics.Bitmap
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class UploadProfileImageUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(userId: String, image: Bitmap): Resource<String> = mainRepository.uploadProfileImage(userId, image)
}