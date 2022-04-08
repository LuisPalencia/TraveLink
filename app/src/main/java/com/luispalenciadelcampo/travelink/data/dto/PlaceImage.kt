package com.luispalenciadelcampo.travelink.data.dto

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceImage(
    @get:Exclude
    val image: Bitmap? = null,
    @get:Exclude
    val attribution: String? = null
) : Parcelable
