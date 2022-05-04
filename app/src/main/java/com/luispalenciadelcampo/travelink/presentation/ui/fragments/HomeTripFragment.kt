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
import com.google.firebase.auth.FirebaseAuth
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.databinding.FragmentHomeTripBinding
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.Resource
import com.luispalenciadelcampo.travelink.utils.observeOnce
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

        val tripUnw = arguments?.getParcelable<Trip>(Constants.BUNDLE_TRIP)
        if(tripUnw == null){
            Log.e(TAG, "HomeTripFragment received a null Trip object from the arguments")
            supportFragmentManager.popBackStackFragment()
            return null
        }

        this.trip = tripUnw

        getEventsTrip()

        setView()
        setButtons()
        setObserverGetPlacePhoto()

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
        //binding.textViewTripName.text = trip.name
        // Sets the toolbar title
        binding.collapsingToolbar.title = trip.name

        if (trip.imageUrl?.isNotEmpty() == true) {
            Glide.with(this.requireContext())
                .load(trip.imageUrl)
                .into(binding.imageViewTrip)
        }else{
            Glide.with(this.requireContext())
                .load(R.drawable.trip1)
                .into(binding.imageViewTrip)
        }

        binding.loadingAnimation.isVisible = false
    }

    private fun setButtons(){
        binding.cardViewEvents.setOnClickListener {
            supportFragmentManager.showEventsTrip(trip.id)
        }



        binding.cardViewCreateEvent.setOnClickListener {
            supportFragmentManager.createEventFromHomeFragment(trip.id)
        }

        binding.cardViewMap.setOnClickListener {
            supportFragmentManager.showEventsMap(trip.id)
        }

        binding.cardViewExpenses.setOnClickListener {
            supportFragmentManager.showExpenses(trip.id)
        }

        binding.cardViewRating.setOnClickListener {
            setObserverRateTrip()
            supportFragmentManager.rateTrip(trip.id)
        }

        binding.cardViewPhotoTrip.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.getTripImage(this@HomeTripFragment.trip, FirebaseAuth.getInstance().currentUser!!.uid)
            }
        }

        binding.btnRemoveTrip.setOnClickListener {
            MaterialAlertDialogBuilder(this.requireContext())
                .setTitle(getString(R.string.dialog_remove_trip_title))
                .setMessage(getString(R.string.dialog_remove_trip_message))
                .setPositiveButton(getString(R.string.ok)){ _, _ ->
                    setObserverRemoveTrip()
                    lifecycleScope.launch {
                        mainViewModel.removeTrip(trip)
                    }
                }
                .setNegativeButton(getString(R.string.cancel)){ dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setObserverRemoveTrip(){
        mainViewModel.removeTripStatus.observeOnce(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this.requireContext(), getString(R.string.trip_removed_successfully), Toast.LENGTH_LONG).show()
                    supportFragmentManager.popBackStackFragment()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.coordinatorLayout, getString(R.string.error_trip_removed), Snackbar.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun setObserverRateTrip(){
        mainViewModel.rateTripStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    Snackbar.make(binding.coordinatorLayout, getString(R.string.rate_trip_success), Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.coordinatorLayout, getString(R.string.rate_trip_error), Snackbar.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun setObserverGetPlacePhoto(){
        mainViewModel.getTripPlacePhotoStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.loadingAnimation.isVisible = false

                    Glide.with(this.requireContext())
                        .load(result.data)
                        .into(binding.imageViewTrip)
                    Snackbar.make(binding.coordinatorLayout, getString(R.string.trip_photo_obtained_message), Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.loadingAnimation.isVisible = false
                    Snackbar.make(binding.coordinatorLayout, getString(R.string.error_get_trip_photo), Snackbar.LENGTH_SHORT).show()
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

}