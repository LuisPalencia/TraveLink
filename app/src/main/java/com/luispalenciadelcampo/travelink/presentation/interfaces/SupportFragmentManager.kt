package com.luispalenciadelcampo.travelink.presentation.interfaces

import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip

interface SupportFragmentManager {
    fun tripSelected(id: String)
    fun createTrip()
    fun userConfigSelected()
    fun createEvent(id: String)
    fun editEvent(trip: Trip, event: Event)
    fun createEventFromHomeFragment(id: String)
    fun showEventsTrip(id: String)
    fun showEventsMap(id: String)
    fun showExpenses(id: String)
    fun rateTrip(id: String)
    fun rateEvent(event: Event, tripId: String)
    fun eventSelected(idTrip: String, idEvent: String)
    fun eventSelectedFromExpenses(idTrip: String, idEvent: String)
    fun showEventLocation(event: Event)
    fun signOutUser()
    fun showAccountInformation()
    fun showContactDetails()
    fun popBackStackFragment()
}