package com.luispalenciadelcampo.travelink.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class User(
    var uuid: String = "",
    var name: String = "",
    var lastname: String = "",
    var birthday: Date = Date(),
    var profileImageUrl: String = "",
    //var phoneNumber: String? = ""
) : Parcelable
