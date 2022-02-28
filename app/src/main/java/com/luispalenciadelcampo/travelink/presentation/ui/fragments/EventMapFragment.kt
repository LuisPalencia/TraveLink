package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var map: GoogleMap

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

    private fun setDeviceLocation(){

    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0

        val latLng = LatLng(event.place.latitude, event.place.longitude)
        map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(event.name)
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0F))

        setDeviceLocation()
    }


}