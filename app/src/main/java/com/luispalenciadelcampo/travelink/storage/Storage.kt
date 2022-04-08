package com.luispalenciadelcampo.travelink.storage

import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.storage.FirebaseStorage
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.data.dto.User

object Storage {

    // This var is in order to control if the data has been downloaded from the DB
    var isDataAvailable = false

    // Places client
    lateinit var placesClient: PlacesClient

    // Firebase storage
    lateinit var firebaseStorage: FirebaseStorage

    // User logged
    lateinit var user: User

    //Data of app:
    //List of Trips
    lateinit var trips: MutableList<Trip>
    //List of Events
    var events = hashMapOf<String, MutableList<Event>>()



    fun clearData(){
        isDataAvailable = false
        user = User()
        trips = mutableListOf()
    }
}