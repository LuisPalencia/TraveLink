package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel


class EventMapFragment : Fragment(), OnMapReadyCallback {

    private val TAG = "ProfileFragment"
    private lateinit var rootView: View

    private var map: GoogleMap? = null

    private val mainViewModel: MainViewModel by activityViewModels()

    private var event = Event()

    private var _binding: FragmentEventMapBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val idEvent = arguments?.getString(Constants.BUNDLE_ID_EVENT_SELECTED)
        if(idEvent != null){
            val eventTemp = mainViewModel.getEventById(idEvent)
            if(eventTemp != null){
                this.event = eventTemp
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEventMapBinding.inflate(inflater, container, false)
        rootView = binding.root

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

    @SuppressLint("MissingPermission")
    private fun setDeviceLocation(){
        if(this.map == null){
            return
        }

        try{
            if(ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED)){
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled  = true
            }else{
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false

            }
        }catch (e: SecurityException){

        }


    }


    private fun checkLocationPermission(){
        when {
            //Permissions are granted
            ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED) -> {
                setDeviceLocation()
            }
            //Permissions are not granted and the dialog in order to request them is not showed
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(this.requireContext(), getString(R.string.error_message_permission_location), Toast.LENGTH_LONG).show()
            }
            //Permissions are not granted and the dialog in order to request them can be showed
            else -> {
                // Request the permission

            }
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

            checkLocationPermission()
        }

    }


}