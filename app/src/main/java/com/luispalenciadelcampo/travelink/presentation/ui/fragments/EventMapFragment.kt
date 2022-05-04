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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.databinding.FragmentEventMapBinding
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import java.io.IOException


class EventMapFragment : Fragment(), OnMapReadyCallback {

    private val TAG = "EventMapFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private var map: GoogleMap? = null

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var event: Event

    private var _binding: FragmentEventMapBinding? = null
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
        _binding = FragmentEventMapBinding.inflate(inflater, container, false)
        rootView = binding.root

        val eventUnw = arguments?.getParcelable<Event>(Constants.BUNDLE_EVENT)
        if(eventUnw == null){
            Log.e(TAG, "EventMapFragment received a null Event object from the arguments")
            supportFragmentManager.popBackStackFragment()
            return null
        }

        this.event = eventUnw

        setUpMap()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpMap(){
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

    override fun onMapReady(p0: GoogleMap) {
        this.map = p0
        if(this.map != null){
            this.map!!.uiSettings.isZoomControlsEnabled = true
            this.map!!.uiSettings.isCompassEnabled = true

            val latLng = LatLng(event.place.latitude, event.place.longitude)
            this.map!!.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(event.name)
            )
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0F))

            checkLocationPermissionAndSetDeviceLocation()
        }

    }


}