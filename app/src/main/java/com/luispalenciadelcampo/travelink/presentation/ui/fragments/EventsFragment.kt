package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.databinding.FragmentEventsBinding
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.ui.adapters.EventsAdapter
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.Resource
import java.io.IOException


class EventsFragment : Fragment() {

    private val TAG = "EventsFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var trip: Trip

    private lateinit var adapter: EventsAdapter

    private var _binding: FragmentEventsBinding? = null
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
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        rootView = binding.root

        val tripUnw = arguments?.getParcelable<Trip>(Constants.BUNDLE_TRIP)
        if(tripUnw == null){
            Log.e(TAG, "EventsFragment received a null Trip object from the arguments")
            supportFragmentManager.popBackStackFragment()
            return null
        }

        this.trip = tripUnw

        setRecyclerView()
        setObserver()
        setButtons()

        return rootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclerView(){
        adapter = EventsAdapter(this.requireContext(), trip)
        adapter.setEventsListener(object : EventsAdapter.EventsListener{
            override fun onItemClick(position: Int, idEvent: String) {
                Log.d(TAG, "Event selected: $idEvent")
                supportFragmentManager.eventSelected(trip.id, idEvent)
            }
        })

        binding.eventsRecyclerView.adapter = adapter
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun setObserver(){
        mainViewModel.eventsTrip.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "INFO OBTAINED")
            when (result) {
                is Resource.Success -> {
                    Log.d(TAG, result.data.toString())
                    if(result.data.size > 0){
                        binding.eventsRecyclerView.visibility = View.VISIBLE
                        binding.layoutNoEvents.layoutNoEvents.visibility = View.GONE

                        adapter.addHeaderAndSubmitList(result.data)
                        //adapter.notifyDataSetChanged()
                    }else{
                        binding.eventsRecyclerView.visibility = View.GONE
                        binding.layoutNoEvents.layoutNoEvents.visibility = View.VISIBLE
                    }

                }
                is Resource.Error -> {
                    Log.d(TAG, "Error when trying to get the trips: ${result.message}")
                }
                is Resource.Loading -> {

                }
            }
        }
    }

    private fun setButtons(){
        binding.btnCreateEvent.setOnClickListener {
            supportFragmentManager.createEvent(trip.id)
        }

        binding.layoutNoEvents.btnCreateNewEvent.setOnClickListener {
            supportFragmentManager.createEvent(trip.id)
        }
    }




}