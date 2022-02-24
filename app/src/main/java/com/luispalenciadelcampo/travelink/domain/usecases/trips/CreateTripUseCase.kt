package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject

class CreateTripUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(trip: Trip): Resource<Trip> = mainRepository.createTrip(trip)
}