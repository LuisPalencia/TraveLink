package com.luispalenciadelcampo.travelink.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Event(
    var id: String = "",
    var name: String? = "",
    var day: Int = 0,
    var startTime: Date = Date(),
    var endTime: Date = Date(),
    var description: String = "",
    var rating: Double = 0.0,
    var city: String = "", // ID of the city Place object
    var place: TripPlace = TripPlace(),
    var price: Double = 0.0,
    var imageUrl: String? = "",
    //@get:Exclude
    //var imageEvent: PlaceImage? = null
) : Parcelable
