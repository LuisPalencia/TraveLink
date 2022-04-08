package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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
import com.luispalenciadelcampo.travelink.utils.Resource
import com.luispalenciadelcampo.travelink.utils.TripFunctions
import com.luispalenciadelcampo.travelink.utils.observeOnce
import kotlinx.coroutines.launch
import java.io.IOException


class EventDetailsFragment : Fragment() {

    private val TAG = "EventDetailsFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private var event = Event()
    private lateinit var trip: Trip

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        rootView = binding.root


        val tripUnw = arguments?.getParcelable<Trip>(Constants.BUNDLE_TRIP)
        if(tripUnw == null){
            Log.e(TAG, "EventDetailsFragment received a null Trip object from the arguments")
            supportFragmentManager.popBackStackFragment()
            return null
        }

        this.trip = tripUnw

        val eventUnw = arguments?.getParcelable<Event>(Constants.BUNDLE_EVENT)
        if(eventUnw == null){
            Log.e(TAG, "EventDetailsFragment received a null Event object from the arguments")
            supportFragmentManager.popBackStackFragment()
            return null
        }

        this.event = eventUnw

        setView()
        setButtons()
        setObserverGetPlacePhoto()

        return rootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setView(){
        binding.loadingAnimation.isVisible = false

        binding.textViewEventName.text = event.name
        binding.textViewDate.text = TripFunctions.getStringForDayPosition(event.day, this.trip.startDate)
        binding.textViewTime.text = "${GenericFunctions.dateHourToString(event.startTime)} - ${GenericFunctions.dateHourToString(event.endTime)}"
        binding.textViewPrice.text = "${event.price} €"

        if (event.imageUrl?.isNotEmpty() == true) {
            Glide.with(this.requireContext())
                .load(event.imageUrl)
                .into(binding.imageViewEvent)
        }else{

        }
    }

    private fun setButtons(){
        binding.cardViewLocationEvent.setOnClickListener {
            supportFragmentManager.showEventLocation(event)
        }

        binding.cardViewPhotoEvent.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.getEventImage(trip.id, this@EventDetailsFragment.event)
            }
        }

        binding.cardViewEditEvent.setOnClickListener {

        }

        binding.cardViewRateEvent.setOnClickListener {

        }

        binding.btnDeleteEvent.setOnClickListener {
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(getString(R.string.dialog_remove_event_title))
                .setMessage(getString(R.string.dialog_remove_event_message))
                .setPositiveButton(getString(R.string.ok)){ _, _ ->
                    setObserverRemoveEvent()
                    lifecycleScope.launch {
                        mainViewModel.removeEvent(event, trip.id)
                    }
                }
                .setNegativeButton(getString(R.string.cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setObserverGetPlacePhoto(){
        mainViewModel.getEventPlacePhotoStatus.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "PHOTO STATUS CHANGED")
            when (result) {
                is Resource.Success -> {
                    binding.loadingAnimation.isVisible = false

                    Glide.with(this.requireContext())
                        .load(result.data)
                        .into(binding.imageViewEvent)
                    Snackbar.make(binding.scrollView, getString(R.string.event_photo_obtained_message), Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.loadingAnimation.isVisible = false
                    Snackbar.make(binding.scrollView, getString(R.string.error_get_event_photo), Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.loadingAnimation.setTextViewVisibility(true)
                    binding.loadingAnimation.setTextStyle(true)
                    binding.loadingAnimation.setTextSize(12F)
                    binding.loadingAnimation.setTextMsg("Getting place photo")
                    binding.loadingAnimation.setEnlarge(5)
                    binding.loadingAnimation.isVisible = true
                }
            }
        }

    }

    private fun setObserverRemoveEvent(){
        mainViewModel.removeEventStatus.observeOnce(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this.requireContext(), getString(R.string.event_removed_successfully), Toast.LENGTH_LONG).show()
                    supportFragmentManager.popBackStackFragment()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.scrollView, getString(R.string.error_event_removed), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

}