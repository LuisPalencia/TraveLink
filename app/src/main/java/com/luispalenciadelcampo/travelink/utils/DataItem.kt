package com.luispalenciadelcampo.travelink.utils

import com.luispalenciadelcampo.travelink.data.dto.Event


sealed class DataItem{
    abstract val id: String
    data class EventItem(val event: Event): DataItem(){
        override val id: String = event.id!!
    }

    data class Header(val idDay: String): DataItem(){
        override val id = idDay
    }
}
