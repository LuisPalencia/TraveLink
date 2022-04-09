package com.luispalenciadelcampo.travelink.utils

import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.data.dto.TripPlace
import java.util.*
import java.util.concurrent.TimeUnit

class TripFunctions {

    companion object{
        fun orderTripsList(trips: MutableList<Trip>): MutableList<Trip>{
            return trips.sortedWith(compareBy({it.startDate}, {it.endDate})).toMutableList()
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

        fun getCityName(idCity: String, cities: MutableList<TripPlace>): String{
            cities.forEach { city ->
                if (city.idPlace == idCity) {
                    return city.name
                }
            }
            return ""
        }

        fun getTripDay(date: Date, startDateTrip: Date): Int{
            val diffDates = date.time - startDateTrip.time
            return TimeUnit.DAYS.convert(diffDates, TimeUnit.MILLISECONDS).toInt()
        }
    }
}