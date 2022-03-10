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
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.luispalenciadelcampo.travelink.utils.Resource
import com.luispalenciadelcampo.travelink.utils.TripFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel(){

    private val TAG = "MainViewModel"

    val user = MutableLiveData<User>()

    val trips = MutableLiveData<Resource<MutableList<Trip>>>()
    val actualTrips = MutableLiveData<MutableList<Trip>>()
    val pastTrips = MutableLiveData<MutableList<Trip>>()
    val createTripStatus = MutableLiveData<Resource<Trip>>()
    val removeTripStatus = MutableLiveData<Resource<Boolean>>()
    val rateTripStatus = MutableLiveData<Resource<Boolean>>()
    private var tripsListenerJob: Job? = null
    val liveDataUpdateTrip = MutableLiveData<Trip>()

    val createEventStatus = MutableLiveData<Resource<Event>>()
    val removeEventStatus = MutableLiveData<Resource<Boolean>>()
    val eventsTrip = MutableLiveData<Resource<MutableList<Event>>>()
    private var eventListenerJob: Job? = null

    suspend fun getUserFromDB(userId: String){
        viewModelScope.launch(Dispatchers.Main) {
            val resultUser = useCase.GetUserInfoUseCase(userId)

            when (resultUser) {
                is Resource.Success -> {
                    user.postValue(resultUser.data!!)
                }
                is Resource.Error -> {
                    Log.d(TAG, "Error when trying to get the user")
                }
                is Resource.Loading -> {

                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun getTrips(userId: String){
        this.tripsListenerJob = viewModelScope.launch {
            useCase.GetTripsUseCase(userId).collect { resultTrips ->
                when (resultTrips) {
                    is Resource.Success -> {
                        Log.d(TAG, resultTrips.data.toString())
                        trips.postValue(resultTrips)
                        Storage.trips = resultTrips.data
                        sortTrips(resultTrips.data)
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

    private fun sortTrips(trips: MutableList<Trip>){
        val actualTripsList = mutableListOf<Trip>()
        val pastTripsList = mutableListOf<Trip>()


        val calendar = GenericFunctions.getActualCalendarWithNoHour()
        val actualDate = calendar.time

        for(trip in trips){
            if(trip.endDate.before(actualDate)){
                pastTripsList.add(trip)
            }else{
                actualTripsList.add(trip)
            }
        }

        this.actualTrips.postValue(actualTripsList)
        this.pastTrips.postValue(pastTripsList)
    }

    @ExperimentalCoroutinesApi
    suspend fun getEvents(trip: Trip){
        this.eventListenerJob = viewModelScope.launch {
            useCase.GetEventsUseCase(trip).collect { resultEvents ->
                when (resultEvents) {
                    is Resource.Success -> {
                        Log.d(TAG, "HOLAAAAAAAA")
                        Log.d(TAG, resultEvents.data.toString())
                        Storage.events[trip.id] = resultEvents.data
                        eventsTrip.postValue(resultEvents)
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

    fun removeTripsListener(){
        if(this.tripsListenerJob != null){
            this.tripsListenerJob!!.cancel()
            this.tripsListenerJob = null
        }
    }

    fun removeEventListener(){
        if(this.eventListenerJob != null){
            this.eventListenerJob!!.cancel()
            this.eventListenerJob = null
        }
    }

    suspend fun createTrip(trip: Trip){
        viewModelScope.launch(Dispatchers.Main) {
            val resultCreateTrip = useCase.CreateTripUseCase(trip)
            createTripStatus.postValue(resultCreateTrip)
        }
    }

    suspend fun removeTrip(trip: Trip){
        viewModelScope.launch(Dispatchers.Main) {
            val resultRemoveTrip = useCase.RemoveTripUseCase(trip)
            removeTripStatus.postValue(resultRemoveTrip)
        }
    }

    suspend fun createEvent(event: Event, idTrip: String){
        viewModelScope.launch(Dispatchers.Main) {
            val resultCreateEvent = useCase.CreateEventUseCase(event, idTrip)
            createEventStatus.postValue(resultCreateEvent)
        }
    }

    suspend fun removeEvent(event: Event, tripId: String){
        viewModelScope.launch(Dispatchers.Main) {
            val resultRemoveEvent = useCase.RemoveEventUseCase(event, tripId)
            removeEventStatus.postValue(resultRemoveEvent)
        }
    }

    suspend fun rateTrip(rating: Double, trip: Trip){
        viewModelScope.launch(Dispatchers.Main) {
            trip.rating = rating
            val resultRateTrip = useCase.RateTrip(trip.id, trip.userAdminId!!, rating)
            rateTripStatus.postValue(resultRateTrip)
        }
    }


    fun getTripById(id: String): Trip?{
        for(trip in Storage.trips){
            if(trip.id == id) return trip
        }
        return null
    }

    /*
    fun selectedTrip(id: String): Trip?{
        for(trip in Storage.trips){
            if(trip.id == id){
                tripSelected.postValue(trip)
                return trip
            }
        }

        return null
    }
     */

    fun getEventById(idEvent: String, idTrip: String): Event?{
        val events = Storage.events[idTrip]
        if(events != null){
            for(event in events){
                if(event.id == idEvent) return event
            }
            return null
        }else{
            return null
        }
    }

    fun getEventsWithExpensesOrdered(idTrip: String): MutableList<Event>{
        val expensesEventList = mutableListOf<Event>()

        if(Storage.events[idTrip] != null){
            for(event in Storage.events[idTrip]!!){
                if(event.price > 0.0){
                    expensesEventList.add(event)
                }
            }

            return TripFunctions.orderEventsListByExpensePrice(expensesEventList)
        }else{
            return expensesEventList
        }
    }

    fun getTotalExpenseTrip(events: MutableList<Event>): Double{
        var totalExpense = 0.0
        for(event in events){
            totalExpense += event.price
        }
        return totalExpense
    }





}