package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class GetTripPhotoUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(trip: Trip, userId: String): Resource<String> = mainRepository.getAndUploadTripImage(trip, userId)
}