package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.databinding.FragmentCreateTripBinding
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.ui.adapters.CitiesAdapter
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.data.dto.TripPlace
import com.luispalenciadelcampo.travelink.presentation.viewmodel.AuthViewModel
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class CreateTripFragment : Fragment() {

    private val TAG = "CreateTripFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: CitiesAdapter

    private var _binding: FragmentCreateTripBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var citiesList = mutableListOf<TripPlace>()
    private var startDate: Date? = null
    private var endDate: Date? = null

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
        _binding = FragmentCreateTripBinding.inflate(inflater, container, false)
        rootView = binding.root

        setRecyclerView()
        setButtons()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Set the logic of the buttons
    private fun setButtons(){
        binding.btnAddCity.setOnClickListener {
            if(citiesList.size < 5){
                searchNewCity()
            }else{
                Snackbar.make(binding.scrollView, getString(R.string.error_cities_max), Snackbar.LENGTH_LONG).show()
            }
        }

        binding.btnCreateTrip.setOnClickListener {
            //binding.editTextTripName.error = null

            if(binding.editTextTripName.text.isBlank()){
                Log.d(TAG, "Empty trip name")
                binding.editTextTripName.error = getString(R.string.error_name_trip_empty)
                return@setOnClickListener
            }else{
                binding.editTextTripName.error = null
            }

            if(startDate == null){
                Snackbar.make(binding.scrollView, getString(R.string.error_start_date_not_selected), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }else if(endDate == null){
                Snackbar.make(binding.scrollView, getString(R.string.error_end_date_not_selected), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else if(startDate!! > endDate) {
                Snackbar.make(binding.scrollView, getString(R.string.error_end_date_before_start_date), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            } else if(citiesList.isEmpty()){
                Snackbar.make(binding.scrollView, getString(R.string.error_cities_not_added), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val diffDates = endDate!!.time - startDate!!.time
            val totalDaysTrip = TimeUnit.DAYS.convert(diffDates, TimeUnit.MILLISECONDS)

            if(totalDaysTrip > Constants.MAXIMUM_DAYS_TRIP){
                Snackbar.make(binding.scrollView, getString(R.string.error_max_days_exceeded), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Create trip
            val trip = Trip(
                userAdminId = FirebaseAuth.getInstance().currentUser!!.uid,
                name = binding.editTextTripName.text.toString(),
                cities = citiesList,
                startDate = startDate!!,
                endDate = endDate!!
            )

            lifecycleScope.launch {
                mainViewModel.createTrip(trip)
            }
        }

        binding.editTextTripDateRange.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            builder.setTitleText("Select a date for the trip")

            val materialDatePicker = builder.build()

            materialDatePicker.show(parentFragmentManager, "dateRangePicker")

            materialDatePicker.addOnPositiveButtonClickListener {
                this.startDate = Date(it.first)
                this.endDate = Date(it.second)
                binding.editTextTripDateRange.setText("${GenericFunctions.dateToString(this.startDate!!)} - ${GenericFunctions.dateToString(this.endDate!!)}")
            }

        }

    }

    // Method that sets the recycler view of the basket fragment
    private fun setRecyclerView(){
        // Create the adapter
        adapter = CitiesAdapter(this.requireContext())

        adapter.setOnItemClickListener(object : CitiesAdapter.OnItemClickListener{
            //Listener when a city element is removed
            override fun onRemoveCityClick(position: Int) {
                citiesList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        })

        // Set the adapter and the layout manager for the recyclerview
        binding.recyclerViewCities.adapter = adapter
        binding.recyclerViewCities.layoutManager = LinearLayoutManager(this.context)
    }

    private fun onDateRangeSelected(day: Int, month: Int, year: Int) {
        startDate = GenericFunctions.createSimpleDateFormat().parse("$day/$month/$year")
        binding.editTextTripDateRange.setText("$day/$month/$year")
        Log.d(TAG, "Start date: ${GenericFunctions.dateToString(startDate!!)}")
    }

    private fun searchNewCity(){

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)



        // Start the autocomplete intent.
        // Adds a filter in order to only search for cities
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setTypeFilter(TypeFilter.CITIES)
            .build(context)

        resultLauncherAutocompleteCity.launch(intent)
        //startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private var resultLauncherAutocompleteCity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // There are no request codes

        Log.d(TAG, "Result code: ${result.resultCode}")

        when(result.resultCode){
            Activity.RESULT_OK -> {
                result.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    addCity(place)
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                result.data?.let {
                    val status = Autocomplete.getStatusFromIntent(it)
                    status.statusMessage?.let { message -> Log.i(TAG, message) }
                    Snackbar.make(binding.scrollView, getString(R.string.error_adding_city), Snackbar.LENGTH_LONG).show()
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.i(TAG, "User cancelled the action")
            }
        }
    }

    // Method that adds a new city
    private fun addCity(place: Place){
        for (city in citiesList) {
            if(city.idPlace == place.id){
                Snackbar.make(binding.scrollView, getString(R.string.error_cities_already_added), Snackbar.LENGTH_LONG).show()
                return
            }
        }

        val city = TripPlace(idPlace = place.id,
            name = place.name,
            place = place,
            latitude = (place.latLng as LatLng).latitude,
            longitude = (place.latLng as LatLng).longitude
        )

        citiesList.add(city)
        adapter.setCitiesList(citiesList)
        adapter.notifyItemInserted(citiesList.size - 1)
    }



}