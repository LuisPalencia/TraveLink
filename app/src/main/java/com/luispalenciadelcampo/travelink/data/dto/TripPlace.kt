package com.luispalenciadelcampo.travelink.data.dto

import android.os.Parcelable
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class TripPlace(
    var idPlace: String = "",
    var name: String = "",
    //var latLng: LatLng?,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    @get:Exclude
    var place: Place? = null,
) : Parcelable
