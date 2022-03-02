package com.luispalenciadelcampo.travelink.domain.usecases.trips

import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.User
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.utils.Resource
import javax.inject.Inject


class RemoveEventUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(event: Event, tripId: String): Resource<Boolean> = mainRepository.removeEvent(event, tripId)
}