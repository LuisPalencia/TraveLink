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
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.databinding.FragmentExpensesBinding
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.ui.adapters.ExpensesAdapter
import com.luispalenciadelcampo.travelink.presentation.ui.adapters.TripsAdapter
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import java.io.IOException


class ExpensesFragment : Fragment() {
    private val TAG = "ExpensesFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private lateinit var trip: Trip

    // Adapter
    private lateinit var adapter: ExpensesAdapter
    private lateinit var eventsWithExpenses: MutableList<Event>

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentExpensesBinding? = null
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
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        rootView = binding.root

        val tripUnw = arguments?.getParcelable<Trip>(Constants.BUNDLE_TRIP)
        if(tripUnw == null){
            Log.e(TAG, "ExpensesFragment received a null Trip object from the arguments")
            supportFragmentManager.popBackStackFragment()
            return null
        }

        this.trip = tripUnw

        setUpView()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpView(){
        this.eventsWithExpenses = mainViewModel.getEventsWithExpensesOrdered(this.trip.id)

        binding.textViewTotalCost.text = "${mainViewModel.getTotalExpenseTrip(this.eventsWithExpenses)} €"

        setRecyclerView()
    }

    // Method that sets the recycler view of the favourite sneakers fragment
    private fun setRecyclerView(){
        // Create the adapter
        adapter = ExpensesAdapter(this.requireContext())

        adapter.setOnItemClickListener(object : ExpensesAdapter.OnItemClickListener{
            // When an item is selected, it opens the sneaker details
            override fun onItemClick(event: Event) {
                Log.d(TAG, "Event selected: ${event.name}")
                event.id?.let { supportFragmentManager.eventSelectedFromExpenses(trip.id, it) }
            }
        })

        adapter.setExpensesList(this.eventsWithExpenses)

        // Set the adapter and the layout manager for the recyclerview
        binding.recyclerViewExpenses.adapter = adapter
        binding.recyclerViewExpenses.layoutManager = LinearLayoutManager(this.context)
    }


}