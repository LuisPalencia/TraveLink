package com.luispalenciadelcampo.travelink.utils

sealed class ChildEventResponse<T>(val data: T? = null, val message: String? = null){
    class ChildAdded<T>(data: T): ChildEventResponse<T>(data)
    class ChildChanged<T>(data: T): ChildEventResponse<T>(data)
    class ChildRemoved<T>(data: T): ChildEventResponse<T>(data)
    class ChildMoved<T>(data: T): ChildEventResponse<T>(data)
    class Cancelled<T>(message: String, data: T? = null): ChildEventResponse<T>(data, message)
}