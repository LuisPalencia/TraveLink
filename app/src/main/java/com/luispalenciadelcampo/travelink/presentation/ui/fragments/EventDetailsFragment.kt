package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.databinding.FragmentEventDetailsBinding
import com.luispalenciadelcampo.travelink.databinding.FragmentHomeTripBinding
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.luispalenciadelcampo.travelink.utils.TripFunctions
import java.io.IOException


class EventDetailsFragment : Fragment() {

    private val TAG = "EventDetailsFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private var event = Event()

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentEventDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Get the MainAcitivity reference with the interface SupportFragmentManager
        try{
            supportFragmentManager = context as MainActivity
        }catch (e: IOException){
            Log.d(TAG, "MainActivity is on null state")
        }

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
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        rootView = binding.root
        // Inflate the layout for this fragment

        setView()
        setButtons()

        return rootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setView(){
        binding.textViewEventName.text = event.name
        binding.textViewDate.text = TripFunctions.getStringForDayPosition(event.day, mainViewModel.tripSelected.value!!.startDate)
        binding.textViewTime.text = "${GenericFunctions.dateHourToString(event.startTime)} - ${GenericFunctions.dateHourToString(event.endTime)}"
        binding.textViewPrice.text = "${event.price} €"
    }

    private fun setButtons(){
        binding.btnShowMap.setOnClickListener {

        }
    }

}