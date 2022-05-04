package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.util.TimeFormatException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.data.dto.TripPlace
import com.luispalenciadelcampo.travelink.databinding.FragmentCreateEventBinding
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit


class CreateEventFragment : Fragment() {

    private val TAG = "CreateEventFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var trip: Trip

    private var place: TripPlace? = null
    private var eventDate: Date? = null
    private var startTime: Date? = null
    private var endTime: Date? = null
    private lateinit var cityId: String


    private var _binding: FragmentCreateEventBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        rootView = binding.root

        val tripUnw = arguments?.getParcelable<Trip>(Constants.BUNDLE_TRIP)
        if(tripUnw == null){
            Log.e(TAG, "CreateEventFragment received a null Trip object from the arguments")
            supportFragmentManager.popBackStackFragment()
            return null
        }

        this.trip = tripUnw


        setupView()
        setButtons()

        return rootView
    }

    private fun setupView(){
        // Configuration for the Price EditText
        binding.layoutFormEvent.editTextPrice.setLocale(Locale.getDefault().toLanguageTag())

        // Configuration for the spinner that chooses the city
        val cityNamesList = mutableListOf<String>()

        // Add the cities to the list
        for(cityItem in trip.cities){

            cityNamesList.add(cityItem.name)
        }

        val adapter = ArrayAdapter(this.requireContext(), R.layout.support_simple_spinner_dropdown_item, cityNamesList)
        binding.layoutFormEvent.spinnerCity.adapter = adapter

        binding.layoutFormEvent.spinnerCity.setSelection(0)
        cityId = trip.cities[0].idPlace

        binding.layoutFormEvent.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                cityId = trip.cities[position].idPlace
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    // Set the logic of the buttons
    private fun setButtons(){
        binding.layoutFormEvent.editTextDate.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val constraintsBuilderRange = CalendarConstraints.Builder()

            val dateValidatorMin = DateValidatorPointForward.from(trip.startDate.time)
            // Add one day to the endDate, as the DateValidatorPointBackward don't enable the last day indicated
            val dateValidatorMax = DateValidatorPointBackward.before(trip.endDate.time + (1000 * 60 * 60 * 24))

            val listValidators = arrayListOf(dateValidatorMin, dateValidatorMax)
            val validators = CompositeDateValidator.allOf(listValidators)
            constraintsBuilderRange.setValidator(validators)

            // Set the calendar in the month where the trip starts
            constraintsBuilderRange.setOpenAt(trip.startDate.time)

            builder.setCalendarConstraints(constraintsBuilderRange.build())

            val datePicker = builder.build()
            datePicker.show(parentFragmentManager, datePicker.toString())

            datePicker.addOnPositiveButtonClickListener {
                this.eventDate = Date(it)
                binding.layoutFormEvent.editTextDate.setText(GenericFunctions.dateToString(this.eventDate!!))
            }
        }

        binding.layoutFormEvent.editTextStartTime.setOnClickListener {
            val materialTimePicker = createTimePicker()
            materialTimePicker.show(parentFragmentManager, "timePicker")

            materialTimePicker.addOnPositiveButtonClickListener {
                val calendar = GenericFunctions.getCalendarWithNoDate()
                calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.hour)
                calendar.set(Calendar.MINUTE, materialTimePicker.minute)
                startTime = calendar.time
                binding.layoutFormEvent.editTextStartTime.setText("${GenericFunctions.dateHourToString(startTime!!)}")
            }
        }

        binding.layoutFormEvent.editTextEndTime.setOnClickListener {
            val materialTimePicker = createTimePicker()
            materialTimePicker.show(parentFragmentManager, "timePicker")

            materialTimePicker.addOnPositiveButtonClickListener {
                val calendar = GenericFunctions.getCalendarWithNoDate()
                calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.hour)
                calendar.set(Calendar.MINUTE, materialTimePicker.minute)
                endTime = calendar.time
                binding.layoutFormEvent.editTextEndTime.setText("${GenericFunctions.dateHourToString(endTime!!)}")
            }
        }

        binding.layoutFormEvent.editTextPlace.setOnClickListener {
            selectPlace()
        }

        binding.layoutFormEvent.btnCreateTrip.setOnClickListener {
            if(binding.layoutFormEvent.editTextEventName.text.isBlank()){
                binding.layoutFormEvent.editTextEventName.error = getString(R.string.error_name_event_empty)
                return@setOnClickListener
            }else {
                binding.layoutFormEvent.editTextEventName.error = null
            }

            if(this.eventDate == null){
                Snackbar.make(binding.scrollView, getString(R.string.error_date_event_empty), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(this.startTime == null){
                Snackbar.make(binding.scrollView, getString(R.string.error_start_time_empty), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(this.endTime == null){
                Snackbar.make(binding.scrollView, getString(R.string.error_end_time_empty), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(this.startTime!! >= this.endTime){
                Snackbar.make(binding.scrollView, getString(R.string.error_end_time_before_start_time), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(this.place == null){
                Snackbar.make(binding.scrollView, getString(R.string.error_place_event_empty), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.layoutFormEvent.editTextPrice.text?.isBlank() == true){
                binding.layoutFormEvent.editTextPrice.error = getString(R.string.error_price_event_empty)
                return@setOnClickListener
            }else {
                binding.layoutFormEvent.editTextPrice.error = null
            }

            val diffDates = eventDate!!.time - trip.startDate.time
            val eventDay = TimeUnit.DAYS.convert(diffDates, TimeUnit.MILLISECONDS).toInt()
            Log.d(TAG, "Event day: $eventDay")

            val event = Event(
                name = binding.layoutFormEvent.editTextEventName.text.toString(),
                day = eventDay,
                startTime = this.startTime!!,
                endTime = this.endTime!!,
                city = this.cityId,
                place = this.place!!,
                price = binding.layoutFormEvent.editTextPrice.getNumericValue(),
                description = binding.layoutFormEvent.editTextEventDescription.text.toString()
            )

            lifecycleScope.launch {
                mainViewModel.createEvent(event, this@CreateEventFragment.trip.id)
            }
        }
    }

    private fun createTimePicker(): MaterialTimePicker{
        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()
    }

    private fun selectPlace(){
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)



        // Start the autocomplete intent.
        // Adds a filter in order to only search for cities
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(context)

        resultLauncherAutocompleteCity.launch(intent)
    }

    private var resultLauncherAutocompleteCity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // There are no request codes

        Log.d(TAG, "Result code: ${result.resultCode}")

        when(result.resultCode){
            Activity.RESULT_OK -> {
                result.data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    this.place = TripPlace(
                        name = place.name,
                        idPlace = place.id,
                        place = place,
                        latitude = (place.latLng as LatLng).latitude,
                        longitude = (place.latLng as LatLng).longitude
                    )
                    binding.layoutFormEvent.editTextPlace.setText(place.name)
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
}