package com.luispalenciadelcampo.travelink.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.dto.*
import com.luispalenciadelcampo.travelink.domain.repository.MainRepository
import com.luispalenciadelcampo.travelink.storage.Storage
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.luispalenciadelcampo.travelink.utils.Resource
import com.luispalenciadelcampo.travelink.utils.TripFunctions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : MainRepository {

    private val TAG = "MainRepositoryImpl"

    override suspend fun getUser(userId: String): Resource<User> {
        return try{
            val user = User()

            val userRef = firebaseDatabase.getReference("${Constants.DB_REFERENCE_USERS}/$userId")

            userRef.get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val result = task.result

                    //Get all data
                    result?.children?.forEach { snapshot ->
                        when(snapshot.key){
                            "name" -> user.name = snapshot.value?.toString() ?: ""
                            "lastName" -> user.lastname = snapshot.value?.toString() ?: ""
                        }
                    }
                }else{
                    throw Exception()
                }
            }
            Resource.Success(user)
        }catch (e: Exception){
            Resource.Error(Constants.RESULT_GET_USER_ERROR)
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getTrips(userId: String): Flow<Resource<MutableList<Trip>>> = callbackFlow {
        var tripsList: MutableList<Trip>
        val tripsRef = firebaseDatabase.getReference("${Constants.DB_REFERENCE_TRIPS}/$userId")
        val tripsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tripsList = mutableListOf()
                snapshot.children.forEach { trips ->
                    val trip = Trip()

                    trip.id = trips.key.toString()
                    trip.userAdminId = snapshot.key.toString()
                    trips.children.forEach{
                        when(it.key){
                            "name" -> trip.name = it.value.toString() ?: ""
                            "startDate" -> {
                                trip.startDate = GenericFunctions.stringToDate(it.value.toString())
                            }
                            "endDate" -> {
                                trip.endDate = GenericFunctions.stringToDate(it.value.toString())
                            }
                            "rating" -> trip.rating = it.value.toString().toIntOrNull() ?: 0
                            "cities" -> {
                                /*val gson = GsonBuilder().setPrettyPrinting().create()
                                val sType = object : TypeToken<List<City>>() { }.type
                                trip.cities = gson.fromJson(snapshot.value?.toString() ?: "", sType)

                                 */

                                for(child in it.children){
                                    val city = TripPlace()
                                    for(itemCity in child.children){
                                        when(itemCity.key) {
                                            "idPlace" -> city.idPlace = itemCity.value.toString() ?: ""
                                            "name" -> city.name = itemCity.value.toString() ?: ""
                                            "latitude" -> city.latitude = itemCity.value.toString().toDoubleOrNull() ?: 0.0
                                            "longitude" -> city.longitude = itemCity.value.toString().toDoubleOrNull() ?: 0.0
                                        }
                                    }
                                    trip.cities.add(city)
                                }
                            }
                        }
                    }
                    tripsList.add(trip)
                }

                //trySend(Resource.Success(tripsList))
                this@callbackFlow.trySendBlocking(Resource.Success(tripsList))

            }

            override fun onCancelled(error: DatabaseError) {
                //trySend(Resource.Error(error.message))
                trySendBlocking(Resource.Error(error.message))

            }

        }

        tripsRef.addValueEventListener(tripsListener)
        awaitClose { tripsRef.removeEventListener(tripsListener) }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getEvents(trip: Trip): Flow<Resource<MutableList<Event>>>  = callbackFlow {
        var eventsList: MutableList<Event>
        val eventsRef = firebaseDatabase.getReference("${Constants.DB_REFERENCE_EVENTS}/${trip.id}")
        val eventsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "INFO OBTAINED: $snapshot")
                eventsList = mutableListOf()
                snapshot.children.forEach { events ->
                    val event = Event()

                    event.id = events.key.toString()

                    events.children.forEach{
                        when(it.key){
                            "name" -> event.name = it.value.toString() ?: ""
                            "day" -> event.day = it.value.toString().toIntOrNull() ?: 0
                            "startTime" -> event.startTime = GenericFunctions.stringToDateHour(it.value.toString())
                            "endTime" -> event.endTime = GenericFunctions.stringToDateHour(it.value.toString())
                            "description" -> event.description = it.value.toString() ?: ""
                            "rating" -> event.rating = it.value.toString().toIntOrNull() ?: 0
                            "city" -> event.city = it.value.toString() ?: ""
                            "place" -> {
                                for(child in it.children){
                                    when(child.key) {
                                        "idPlace" -> event.place.idPlace = child.value.toString()
                                        "name" -> event.place.name = child.value.toString()
                                        "latitude" -> event.place.latitude = child.value.toString().toDoubleOrNull() ?: 0.0
                                        "longitude" -> event.place.longitude = child.value.toString().toDoubleOrNull() ?: 0.0
                                    }
                                }
                            }
                            "price" -> event.price = it.value.toString().toDoubleOrNull() ?: 0.0
                        }
                    }
                    eventsList.add(event)
                }

                eventsList = TripFunctions.orderEventsList(eventsList)
                this@callbackFlow.trySendBlocking(Resource.Success(eventsList))
            }

            override fun onCancelled(error: DatabaseError) {
                //trySend(Resource.Error(error.message))
                trySendBlocking(Resource.Error(error.message))

            }
        }
        eventsRef.addValueEventListener(eventsListener)
        awaitClose {
            Log.d(TAG, "Removing events listener for trip ${trip.id}")
            eventsRef.removeEventListener(eventsListener)
        }
    }

    /*
    override suspend fun getTrips(userId: String): Resource<MutableList<Trip>> {
        return try{
            var tripsList = mutableListOf<Trip>()
            val tripsRef = firebaseDatabase.getReference("${Constants.DB_REFERENCE_TRIPS}/$userId")
            val tripsChildListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tripsList = mutableListOf()
                    snapshot.children.forEach { trips ->
                        val trip = Trip()

                        trip.id = snapshot.key.toString()
                        trip.userAdminId = snapshot.key.toString()
                        trips.children.forEach{
                            when(it.key){
                                "name" -> trip.name = it.value.toString() ?: ""
                                "startDate" -> {
                                    trip.startDate = GenericFunctions.stringToDate(it.value.toString())
                                }
                                "endDate" -> {
                                    trip.endDate = GenericFunctions.stringToDate(it.value.toString())
                                }
                                "rating" -> trip.rating = it.value.toString().toIntOrNull() ?: 0
                                "cities" -> {
                                    //Log.d(TAG, it.value.toString())
                                    /*val gson = GsonBuilder().setPrettyPrinting().create()
                                    val sType = object : TypeToken<List<City>>() { }.type
                                    trip.cities = gson.fromJson(snapshot.value?.toString() ?: "", sType)

                                     */
                                    /*
                                    for(child in it.children){
                                        Log.d(TAG, child.toString())
                                    }

                                     */
                                }
                            }
                        }
                        tripsList.add(trip)
                    }
                    Log.d(TAG, "UPDATING LIST")
                    Log.d(TAG, tripsList.toString())
                    Resource.Success(tripsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Resource.Error(error.message, null)
                }

            }

            tripsRef.addValueEventListener(tripsChildListener)
            Resource.Success(tripsList)

        }catch (e: Exception){
            Resource.Error(Constants.RESULT_GET_TRIPS_ERROR)
        }
    }

     */

    override suspend fun createTrip(trip: Trip): Resource<Trip> {
        return try {
            val userId = trip.userAdminId ?: throw Exception()
            val tripsUserRef = firebaseDatabase.getReference("${Constants.DB_REFERENCE_TRIPS}/$userId")

            val tripId = tripsUserRef.push().key ?: throw Exception()
            trip.id = tripId

            //Set the map and update it in the DB
            val childUpdates = hashMapOf<String, Any>(
                "name" to trip.name!!,
                "startDate" to GenericFunctions.dateToString(trip.startDate!!),
                "endDate" to GenericFunctions.dateToString(trip.endDate!!),
                "cities" to trip.cities!!,
                "rating" to 0,
            )

            //Log.d(TAG, childUpdates.toString())

            //Perform the DB insertion
            tripsUserRef.child(tripId).updateChildren(childUpdates).await()
            Resource.Success(trip)
        }catch (e: Exception){
            Resource.Error(Constants.RESULT_CREATE_TRIP_ERROR)
        }
    }

    override suspend fun removeTrip(trip: Trip): Resource<Boolean> {
        return try {

            val eventsTripRef = firebaseDatabase.getReference("${Constants.DB_REFERENCE_EVENTS}/${trip.id}")
            eventsTripRef.removeValue().await()

            val userId = trip.userAdminId ?: throw Exception()
            val tripsUserRef = firebaseDatabase.getReference("${Constants.DB_REFERENCE_TRIPS}/$userId/${trip.id}")
            tripsUserRef.removeValue().await()

            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(Constants.RESULT_REMOVE_TRIP_ERROR)
        }
    }

    override suspend fun createEvent(event: Event, tripId: String): Resource<Event> {
        return try {

            val eventsTripRef = firebaseDatabase.getReference("${Constants.DB_REFERENCE_EVENTS}/$tripId")

            val eventId = eventsTripRef.push().key ?: throw Exception()
            event.id = eventId


            //Set the map and update it in the DB
            val childUpdates = hashMapOf<String, Any>(
                "name" to event.name!!,
                "day" to event.day,
                "startTime" to GenericFunctions.dateHourToString(event.startTime!!),
                "endTime" to GenericFunctions.dateHourToString(event.endTime!!),
                "description" to event.description,
                "rating" to 0,
                "city" to event.city,
                "place" to event.place,
                "price" to event.price
            )

            //Perform the DB insertion
            eventsTripRef.child(eventId).updateChildren(childUpdates).await()
            Resource.Success(event)
        }catch (e: Exception){
            Resource.Error(Constants.RESULT_CREATE_TRIP_ERROR)
        }
    }

    override suspend fun removeEvent(event: Event, tripId: String): Resource<Boolean> {
        return try {
            val eventRef = firebaseDatabase.getReference("${Constants.DB_REFERENCE_EVENTS}/$tripId/${event.id}")
            eventRef.removeValue().await()

            Resource.Success(true)
        }catch (e: Exception){
            Resource.Error(Constants.RESULT_REMOVE_EVENT_ERROR)
        }
    }

    suspend fun getPlaceImage(placeId: String): Resource<PlaceImage>{
        // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        val fields = listOf(Place.Field.PHOTO_METADATAS)

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)
        var placeImage: PlaceImage? = null

        Storage.placesClient.fetchPlace(placeRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place

                // Get the photo metadata.
                val metada = place.photoMetadatas
                if (metada == null || metada.isEmpty()) {
                    Log.d(TAG, "No photo metadata.")
                    return@addOnSuccessListener
                }
                val photoMetadata = metada.first()

                // Get the attribution text.
                val attributions = photoMetadata?.attributions

                // Create a FetchPhotoRequest.
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build()
                Storage.placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        placeImage = PlaceImage(fetchPhotoResponse.bitmap, attributions)
                        //Resource.Success(bitmap)
                    }.addOnFailureListener { exception: Exception ->
                        if (exception is ApiException) {
                            Log.e(TAG, "Place not found: " + exception.message)
                            //val statusCode = exception.statusCode
                            Resource.Error<String>(exception.message ?: "Error")
                            return@addOnFailureListener
                        }
                    }
            }
        return Resource.Success(placeImage!!)
    }
}