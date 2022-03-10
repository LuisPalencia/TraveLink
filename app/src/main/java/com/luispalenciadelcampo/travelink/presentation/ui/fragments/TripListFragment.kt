package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.ui.adapters.TripsAdapter
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import java.io.IOException

enum class TripsType {
    CURRENT_TRIP,
    PAST_TRIP
}

class TripListFragment(private val tripsType: TripsType) : Fragment() {

    private val TAG = "TripListCurrentFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private val mainViewModel: MainViewModel by activityViewModels()

    // Adapter
    private lateinit var adapter: TripsAdapter

    // Views
    private lateinit var btnCreateTrip: FloatingActionButton
    private lateinit var tripsRecyclerView: RecyclerView

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
        rootView = inflater.inflate(R.layout.fragment_list_trips, container, false)

        setViews()
        setButtons()
        setRecyclerView()
        setObservers()

        return rootView
    }

    private fun setViews(){
        btnCreateTrip = rootView.findViewById(R.id.btnCreateTrip)
        tripsRecyclerView = rootView.findViewById(R.id.tripsRecyclerView)
    }


    private fun setButtons(){
        btnCreateTrip.setOnClickListener {
            supportFragmentManager.createTrip()
        }
    }

    private fun setObservers(){
        val liveDataTrips: MutableLiveData<MutableList<Trip>> = if(this.tripsType == TripsType.CURRENT_TRIP){
            mainViewModel.actualTrips
        }else{
            mainViewModel.pastTrips
        }

        liveDataTrips.observe(viewLifecycleOwner) { result ->
            adapter.setTripsList(result)
            adapter.notifyDataSetChanged()
            /*
            when (result) {
                is Resource.Success -> {
                    //Log.d(TAG, result.data.toString())
                    adapter.setTripsList(result.data)
                    adapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    Log.d(TAG, "Error when trying to get the trips: ${result.message}")
                }
                is Resource.Loading -> {

                }
            }

             */
        }
    }

    // Method that sets the recycler view of the favourite sneakers fragment
    private fun setRecyclerView(){
        // Create the adapter
        adapter = TripsAdapter(this.requireContext())

        adapter.setOnItemClickListener(object : TripsAdapter.OnItemClickListener{
            // When an item is selected, it opens the sneaker details
            override fun onItemClick(position: Int, idTrip: String) {
                Log.d(TAG, "Trip selected: $idTrip")
                supportFragmentManager.tripSelected(idTrip)
            }
        })

        // Set the adapter and the layout manager for the recyclerview
        tripsRecyclerView.adapter = adapter
        tripsRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

}