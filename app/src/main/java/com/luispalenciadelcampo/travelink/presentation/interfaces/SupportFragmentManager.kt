package com.luispalenciadelcampo.travelink.presentation.interfaces

import com.luispalenciadelcampo.travelink.data.dto.Event

interface SupportFragmentManager {
    fun tripSelected(id: String)
    fun createTrip()
    fun userConfigSelected()
    fun createEvent()
    fun createEventFromHomeFragment()
    fun showEventsTrip()
    fun showEventsMap()
    fun showExpenses()
    fun eventSelected(idEvent: String)
    fun showEventLocation(event: Event)
    fun signOutUser()
    fun popBackStackFragment()
}