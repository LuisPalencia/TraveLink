package com.luispalenciadelcampo.travelink.presentation.interfaces

interface SupportFragmentManager {
    fun tripSelected(id: String)
    fun createTrip()
    fun userConfigSelected()
    fun createEvent()
    fun showEventsTrip()
    fun eventSelected(idEvent: String)
    fun signOutUser()
    fun popBackStackFragment()
}