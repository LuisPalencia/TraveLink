package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.databinding.FragmentEventsMapBinding
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import java.io.IOException


class EventsMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private val TAG = "EventsMapFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View


    private var map: GoogleMap? = null
    private lateinit var eventList: MutableList<Event>
    private var hashMapMarkerEvents = hashMapOf<Marker, String>()
    private var positionEvent = 0


    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var trip: Trip

    private var _binding: FragmentEventsMapBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var requestPermission: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Get the MainAcitivity reference with the interface SupportFragmentManager
        try{
            supportFragmentManager = context as MainActivity
        }catch (e: IOException){
            Log.d(TAG, "MainActivity is on null state")
        }

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            checkLocationPermissionAndSetDeviceLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        _binding = FragmentEventsMapBinding.inflate(inflater, container, false)
        rootView = binding.root

        val tripUnw = arguments?.getParcelable<Trip>(Constants.BUNDLE_TRIP)
        if(tripUnw == null){
            Log.e(TAG, "EventsMapFragment received a null Trip object from the arguments")
            supportFragmentManager.popBackStackFragment()
            return null
        }

        this.trip = tripUnw

        // Get the events of the trip
        val eventsTemp = mainViewModel.getLocalEvents(this.trip)
        // Check if events are not null and not empty
        if(eventsTemp != null && eventsTemp.size > 0){
            this.eventList = eventsTemp
            // Request the map in order to show the events location
            setUpMap()
        }

        return rootView
    }

    // Fun that gets the fragment where the map is going to be placed, and request it
    private fun setUpMap(){
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Fun that set the buttons logic
    private fun setButtons(){
        // This button moves the map to the first event location
        binding.btnFirstEvent.setOnClickListener {
            // Set the actual position of the event
            positionEvent = 0
            // Move the map to the correct location
            moveMap(LatLng(eventList[0].place.latitude, eventList[0].place.longitude))
            // Set the text with the event selected
            setTextEventSelected()
        }

        // This button moves the map to the next event location
        binding.btnNextEvent.setOnClickListener {
            // Check if it is the last event
            if(positionEvent < eventList.size - 1){
                // Set the actual position of the event
                positionEvent++
                // Move the map to the correct location
                moveMap(LatLng(eventList[positionEvent].place.latitude, eventList[positionEvent].place.longitude))
                // Set the text with the event selected
                setTextEventSelected()
            }else{
                // Show error
                Toast.makeText(this.requireContext(), getString(R.string.last_event_error), Toast.LENGTH_SHORT).show()
            }
        }

        // This button moves the map to the previous event location
        binding.btnPreviousEvent.setOnClickListener {
            // Check if it is the first event
            if(positionEvent > 0){
                // Set the actual position of the event
                positionEvent--
                // Move the map to the correct location
                moveMap(LatLng(eventList[positionEvent].place.latitude, eventList[positionEvent].place.longitude))
                // Set the text with the event selected
                setTextEventSelected()
            }else{
                // Show error
                Toast.makeText(this.requireContext(), getString(R.string.first_event_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fun that sets the text of the Event position textview
    private fun setTextEventSelected(){
        binding.textViewEventSelected.setText("${getString(R.string.event_selected)}: ${positionEvent + 1} - ${eventList[positionEvent].name}")
    }

    // Fun that moves the map to the actual position
    private fun moveMap(latLng: LatLng){
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0F))
    }

    // Listener that is executed when the map is ready
    override fun onMapReady(p0: GoogleMap) {
        this.map = p0
        if(this.map != null){
            // Set the logic of the buttons when the map is ready
            setButtons()

            this.map!!.uiSettings.isZoomControlsEnabled = true
            this.map!!.uiSettings.isCompassEnabled = true

            // Move the map to the first event location
            val latLng = LatLng(eventList[0].place.latitude, eventList[0].place.longitude)
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0F))

            // Add a marker for each event at its position
            for(event in eventList){
                val marker = this.map?.addMarker(MarkerOptions()
                    .title(event.name)
                    .position(LatLng(event.place.latitude, event.place.longitude))
                    .snippet(GenericFunctions.dateHourToString(event.startTime))
                )

                // Add the marker and the event id to the hashmap so it is available when a marker is selected
                if(marker != null){
                    hashMapMarkerEvents[marker] = event.id
                }

            }
            // Set the text for the first event
            setTextEventSelected()

            // Set the listener when a marker is selected
            this.map?.setOnInfoWindowClickListener(this)

            // Check the location permission in order to show device's location
            checkLocationPermissionAndSetDeviceLocation()
        }
    }

    // Listener that is executed when a marker is selected
    override fun onInfoWindowClick(p0: Marker) {
        // Get the id of the event selected
        val eventId = hashMapMarkerEvents[p0]
        // Call main activity in order to show the event details
        if (eventId != null) {
            supportFragmentManager.eventSelectedFromMap(trip.id, eventId)
        }
    }

    // Method that shows in the map the device's position
    @SuppressLint("MissingPermission")
    private fun checkLocationPermissionAndSetDeviceLocation(){
        if(this.map == null){
            return
        }

        try{
            when {
                //Permissions are granted
                ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED) -> {
                    map?.isMyLocationEnabled = true
                    map?.uiSettings?.isMyLocationButtonEnabled  = true
                }
                //Permissions are not granted and the dialog in order to request them is not showed
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    map?.isMyLocationEnabled = false
                    map?.uiSettings?.isMyLocationButtonEnabled  = false
                    Log.d(TAG, "Permissions not enabled")
                }
                //Permissions are not granted and the dialog in order to request them can be showed
                else -> {
                    // Request the permission
                    requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }catch (e: SecurityException){
            Log.d(TAG, e.toString())
        }
    }
}