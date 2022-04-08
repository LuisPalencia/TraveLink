package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventPhotoUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(idTrip: String, event: Event): Resource<String> = mainRepository.getAndUploadEventImage(idTrip, event)
}