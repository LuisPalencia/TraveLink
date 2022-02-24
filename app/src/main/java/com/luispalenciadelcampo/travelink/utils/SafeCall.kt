package com.luispalenciadelcampo.travelink.utils

import android.util.Log

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception){
        Log.d("safeCall", "ERROR EN safeCall")
        Log.d("safeCall", e.toString())
        Resource.Error(e.message ?: "An unknow error was raised")
    }
}