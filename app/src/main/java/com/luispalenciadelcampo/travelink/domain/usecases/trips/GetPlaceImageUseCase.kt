package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.data.dto.PlaceImage
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class GetPlaceImageUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(placeId: String): Resource<PlaceImage> = mainRepository.getPlaceImage(placeId)
}