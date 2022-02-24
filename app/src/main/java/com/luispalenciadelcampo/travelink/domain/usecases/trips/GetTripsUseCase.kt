package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTripsUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(userId: String): Flow<Resource<MutableList<Trip>>> = mainRepository.getTrips(userId)
}