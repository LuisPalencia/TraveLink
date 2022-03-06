package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.databinding.FragmentHomeTripBinding
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.IOException


class HomeTripFragment : Fragment() {

    private val TAG = "HomeTripFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var trip: Trip

    private var _binding: FragmentHomeTripBinding? = null
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
        _binding = FragmentHomeTripBinding.inflate(inflater, container, false)
        rootView = binding.root

        trip = mainViewModel.tripSelected.value!!

        getEventsTrip()

        setView()
        setButtons()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        // Remove the event listener
        mainViewModel.removeEventListener()

        super.onDestroy()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getEventsTrip(){
        lifecycleScope.launch {
            mainViewModel.getEvents(trip)
        }
    }


    private fun setView(){
        binding.textViewTripName.text = trip.name
        if(trip.image != null){
            binding.imageViewTrip.setImageBitmap(trip.image)
        }else{
            Glide.with(requireContext())
                .load(R.drawable.trip1)
                .into(binding.imageViewTrip)
        }
    }

    private fun setButtons(){
        binding.cardViewEvents.setOnClickListener {
            supportFragmentManager.showEventsTrip()
        }



        binding.cardViewCreateEvent.setOnClickListener {
            supportFragmentManager.createEventFromHomeFragment()
        }

        binding.cardViewMap.setOnClickListener {
            supportFragmentManager.showEventsMap()
        }

        binding.cardViewExpenses.setOnClickListener {
            supportFragmentManager.showExpenses()
        }

        binding.cardViewRating.setOnClickListener {
            setObserverRateTrip()
            supportFragmentManager.rateTrip()
        }

        binding.cardViewRemoveTrip.setOnClickListener {
            setObserverRemoveTrip()
            lifecycleScope.launch {
                mainViewModel.removeTrip(trip)
            }
        }
    }

    private fun setObserverRemoveTrip(){
        mainViewModel.removeTripStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this.requireContext(), getString(R.string.trip_removed_successfully), Toast.LENGTH_LONG).show()
                    supportFragmentManager.popBackStackFragment()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.scrollView, getString(R.string.error_trip_removed), Snackbar.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun setObserverRateTrip(){
        mainViewModel.rateTripStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    Snackbar.make(binding.scrollView, getString(R.string.rate_trip_success), Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.scrollView, getString(R.string.rate_trip_error), Snackbar.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }


}