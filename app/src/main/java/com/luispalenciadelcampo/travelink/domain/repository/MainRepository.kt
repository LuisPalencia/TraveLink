package com.luispalenciadelcampo.travelink.domain.repository

import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.data.dto.User
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun getUser(userId: String): Resource<User>
    @ExperimentalCoroutinesApi
    suspend fun getTrips(userId: String): Flow<Resource<MutableList<Trip>>>
    suspend fun getEvents(trip: Trip): Flow<Resource<MutableList<Event>>>
    suspend fun createTrip(trip: Trip): Resource<Trip>
    suspend fun createEvent(event: Event, tripId: String): Resource<Event>
}