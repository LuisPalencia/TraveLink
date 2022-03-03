package com.luispalenciadelcampo.travelink.utils

import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import java.util.*

class TripFunctions {

    companion object{
        fun orderTripsList(trips: MutableList<Trip>): MutableList<Trip>{
            return trips.sortedWith(compareBy({it.startDate})).toMutableList()
        }

        fun orderEventsList(events: MutableList<Event>): MutableList<Event>{
            return events.sortedWith(compareBy({it.day}, {it.startTime})).toMutableList()
        }

        fun orderEventsListByExpensePrice(events: MutableList<Event>): MutableList<Event>{
            return events.sortedWith(compareBy<Event> { it.price }.reversed()).toMutableList()
        }

        fun getStringForDayPosition(day: Int, startDate: Date): String{
            val calendar = Calendar.getInstance()
            calendar.time = startDate
            calendar.add(Calendar.DATE, day)
            return GenericFunctions.createSimpleDateFormat().format(calendar.time)
        }
    }
}