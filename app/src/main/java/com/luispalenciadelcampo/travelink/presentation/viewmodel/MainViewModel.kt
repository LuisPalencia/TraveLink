package com.luispalenciadelcampo.travelink.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.data.dto.User
import com.luispalenciadelcampo.travelink.domain.usecases.UseCase
import com.luispalenciadelcampo.travelink.storage.Storage
import com.luispalenciadelcampo.travelink.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel(){

    private val TAG = "MainViewModel"

    val user = MutableLiveData<Resource<User>>()

    val trips = MutableLiveData<Resource<MutableList<Trip>>>()
    val createTripStatus = MutableLiveData<Resource<Trip>>()
    val tripSelected = MutableLiveData<Trip>()
    val liveDataUpdateTrip = MutableLiveData<Trip>()

    val createEventStatus = MutableLiveData<Resource<Event>>()
    val eventsTrip = MutableLiveData<Resource<MutableList<Event>>>()
    val eventSelected = MutableLiveData<Event>()
    private var eventListenerJob: Job? = null

    suspend fun getUserFromDB(userId: String){
        viewModelScope.launch(Dispatchers.Main) {
            val resultUser = useCase.GetUserInfoUseCase(userId)
            user.postValue(resultUser)
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun getTrips(userId: String){
        Log.d(TAG, "Getting events")
        viewModelScope.launch {
            useCase.GetTripsUseCase(userId).collect { resultTrips ->
                when (resultTrips) {
                    is Resource.Success -> {
                        Log.d(TAG, resultTrips.data.toString())
                        trips.postValue(resultTrips)
                        Storage.trips = resultTrips.data

                    }
                    is Resource.Error -> {
                        Log.d(TAG, "Error when trying to get the trips: ${resultTrips.message}")
                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun getEvents(trip: Trip){
        this.eventListenerJob = viewModelScope.launch {
            useCase.GetEventsUseCase(tripSelected.value!!).collect { resultEvents ->
                when (resultEvents) {
                    is Resource.Success -> {
                        Log.d(TAG, resultEvents.data.toString())
                        Storage.events[trip.id] = resultEvents.data

                        // Only update the
                        if(tripSelected.value != null){
                            if(tripSelected.value!!.id == trip.id) eventsTrip.postValue(resultEvents)
                        }
                    }
                    is Resource.Error -> {
                        Log.d(TAG, "Error when trying to get the trips: ${resultEvents.message}")
                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }

    fun getLocalEvents(trip: Trip): MutableList<Event>? {
        return Storage.events[trip.id]
    }

    fun removeEventListener(){
        if(eventListenerJob != null){
            eventListenerJob!!.cancel()
            eventListenerJob = null
        }
    }

    suspend fun createTrip(trip: Trip){
        viewModelScope.launch(Dispatchers.Main) {
            val resultCreateTrip = useCase.CreateTripUseCase(trip)
            createTripStatus.postValue(resultCreateTrip)
        }
    }


    fun selectedTrip(id: String): Trip?{
        for(trip in Storage.trips){
            if(trip.id == id){
                tripSelected.postValue(trip)
                return trip
            }
        }

        return null
    }

    suspend fun createEvent(event: Event){
        viewModelScope.launch(Dispatchers.Main) {
            val resultCreateEvent = useCase.CreateEventUseCase(event, tripSelected.value!!.id)
            createEventStatus.postValue(resultCreateEvent)
        }
    }

    fun getEventById(id: String): Event?{
        val events = Storage.events[tripSelected.value!!.id]
        if(events != null){
            for(event in events){
                if(event.id == id) return event
            }
            return null
        }else{
            return null
        }
    }



}