package com.luispalenciadelcampo.travelink.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var uuid: String? = "",
    var name: String? = "",
    var lastname: String? = "",
    //var phoneNumber: String? = ""
) : Parcelable
