package com.luispalenciadelcampo.travelink.data.dto

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.firebase.database.Exclude
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
    @get:Exclude
    var image: Bitmap? = null
) : Parcelable
