package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.luispalenciadelcampo.travelink.databinding.FragmentTripsBinding
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.ui.adapters.TripsAdapter
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.launch
import java.io.IOException


class TripsFragment : Fragment() {

    private val TAG = "TripsFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: TripsAdapter

    // Adapter
    //private var adapter: TripsAdapter

    private var _binding: FragmentTripsBinding? = null
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
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        rootView = binding.root

        setButtons()
        setObservers()
        setRecyclerView()
        getInitialData()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setButtons(){
        binding.btnCreateTrip.setOnClickListener {
            supportFragmentManager.createTrip()
        }
    }

    private fun setObservers(){
        mainViewModel.trips.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    //Log.d(TAG, result.data.toString())
                    if(result.data != null){
                        adapter.setTripsList(result.data)
                        adapter.notifyDataSetChanged()
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

    private fun getInitialData(){
        lifecycleScope.launch {
            mainViewModel.getTrips(FirebaseAuth.getInstance().currentUser!!.uid)
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
        binding.tripsRecyclerView.adapter = adapter
        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }


}