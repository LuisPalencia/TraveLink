package com.luispalenciadelcampo.travelink.domain.repository

import android.graphics.Bitmap
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.PlaceImage
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.data.dto.User
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun getUser(userId: String): Resource<User>
    @ExperimentalCoroutinesApi
    suspend fun getTrips(userId: String): Flow<Resource<MutableList<Trip>>>
    @ExperimentalCoroutinesApi
    suspend fun getEvents(trip: Trip): Flow<Resource<MutableList<Event>>>
    suspend fun createTrip(trip: Trip): Resource<Trip>
    suspend fun removeTrip(trip: Trip): Resource<Boolean>
    suspend fun createEvent(event: Event, tripId: String): Resource<Event>
    suspend fun removeEvent(event: Event, tripId: String): Resource<Boolean>
    suspend fun rateTrip(tripId: String, userId: String, rating: Double): Resource<Boolean>
    suspend fun getPlaceImage(placeId: String): Resource<PlaceImage>
    suspend fun getAndUploadEventImage(idTrip: String, event: Event): Resource<String>
    suspend fun getAndUploadTripImage(trip: Trip, userId: String): Resource<String>
}