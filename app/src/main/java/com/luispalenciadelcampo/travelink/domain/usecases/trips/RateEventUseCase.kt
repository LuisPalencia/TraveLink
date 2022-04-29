package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class RateEventUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(eventId: String, tripId: String, rating: Double): Resource<Boolean> = mainRepository.rateEvent(eventId, tripId, rating)
}