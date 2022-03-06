package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class RateTrip @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(tripId: String, userId: String, rating: Double): Resource<Boolean> = mainRepository.rateTrip(tripId, userId, rating)
}