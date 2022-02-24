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


        setRecyclerView()
        setObserver()

        return rootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclerView(){
        adapter = EventsAdapter(this.requireContext(), mainViewModel.tripSelected.value!!)
        adapter.setEventsListener(object : EventsAdapter.EventsListener{
            override fun onItemClick(position: Int, idEvent: String) {
                Log.d(TAG, "Event selected: $idEvent")
                supportFragmentManager.eventSelected(idEvent)
            }
        })

        binding.eventsRecyclerView.adapter = adapter
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun setObserver(){
        mainViewModel.eventsTrip.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    Log.d(TAG, result.data.toString())
                    adapter.addHeaderAndSubmitList(result.data)
                    //adapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    Log.d(TAG, "Error when trying to get the trips: ${result.message}")
                }
                is Resource.Loading -> {

                }
            }
        }
    }




}