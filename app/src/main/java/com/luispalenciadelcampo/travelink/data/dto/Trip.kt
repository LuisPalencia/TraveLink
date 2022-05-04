package com.luispalenciadelcampo.travelink.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Trip(
    var id: String = "",
    var userAdminId: String? = "",
    var name: String = "",
    var cities: MutableList<TripPlace> = mutableListOf(),
    var startDate: Date = Date(),
    var endDate: Date = Date(),
    var rating: Double = 0.0,
    var participants: MutableList<String> = mutableListOf(),
    var imageUrl: String? = "",
    //@get:Exclude
    //var image: Bitmap? = null
) : Parcelable
