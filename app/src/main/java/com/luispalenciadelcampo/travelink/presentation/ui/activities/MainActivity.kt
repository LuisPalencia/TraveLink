package com.luispalenciadelcampo.travelink.presentation.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.constants.Constants
import com.luispalenciadelcampo.travelink.databinding.ActivityMainBinding
import com.luispalenciadelcampo.travelink.storage.Storage
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.presentation.ui.fragments.RateDialogFragment
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.Resource
import com.luispalenciadelcampo.travelink.utils.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SupportFragmentManager {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //ViewModels
    private val mainViewModel: MainViewModel by viewModels()

    // Navcontroller for the navigation graph
    private var currentNavController: LiveData<NavController>? = null

    //private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //This extra indicates if user logged manually in the previous activity
        val loginPreviousActivity = intent.getBooleanExtra(Constants.LOGGED_BEFORE_MAIN_ACTIVITY, false)

        //Check if sneaker ID is correct
        if(loginPreviousActivity){
            //If user has just logged in, show a snackbar indicating loggin was successfully
            Snackbar.make(binding.constraintLayoutMainActivity, getString(R.string.correct_login), Snackbar.LENGTH_LONG).show()
        }

        //Check if device is connected to internet
        if(GenericFunctions.isDeviceConnectedToInternet(this)){
            initializePlacesAPI()
            initializeFirebaseStorage()
            initializeMainActivity()
            getInitialData()
            setObservers()
            checkLocationPermission()
        }else{
            //setLayoutNoInternetConnection()
        }
    }

    override fun onDestroy() {
        // Remove the trips listener
        mainViewModel.removeTripsListener()

        super.onDestroy()
    }

    //Method that is executed when the toolbar back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun getInitialData(){
        lifecycleScope.launch {
            mainViewModel.getUserFromDB(FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }

    private fun initializePlacesAPI(){
        // Initialize the SDK
        
        Places.initialize(applicationContext, Constants.PLACES_API_KEY)

        // Create a new PlacesClient instance
        Storage.placesClient = Places.createClient(this)
    }

    private fun initializeFirebaseStorage(){
        Storage.firebaseStorage = Firebase.storage
    }

    // Method that initializes the main activity
    private fun initializeMainActivity(){
        firebaseAuth = FirebaseAuth.getInstance()

        if(!Storage.isDataAvailable){
            //Set the user observer
            //setInitialObserverUser()
            //Get the initial data
            //getInitialData()
            // Now data is available, so set the Storage variable
            Storage.isDataAvailable = true
        }


        //Set the correct visibility of the layouts
        //binding.layoutShop.visibility = View.VISIBLE
        //binding.layoutNoConnection.root.visibility = View.GONE

        // Set the navigation graph of the app in order to keep the progress on each item of the bottomNavigationView
        val navGraphIds = listOf(R.navigation.trips, R.navigation.profile)

        val controller = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        currentNavController = controller
    }

    private fun setObservers(){
        mainViewModel.createTripStatus.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, getString(R.string.trip_created), Toast.LENGTH_LONG).show()
                    popBackStackFragment()
                }
                is Resource.Error -> {
                    Toast.makeText(this, getString(R.string.error_trip_creation), Toast.LENGTH_LONG).show()
                }
            }
        }
        mainViewModel.createEventStatus.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, getString(R.string.event_created), Toast.LENGTH_LONG).show()
                    popBackStackFragment()
                }
                is Resource.Error -> {
                    Toast.makeText(this, getString(R.string.error_event_creation), Toast.LENGTH_LONG).show()
                }
            }
        }

        mainViewModel.updateEventStatus.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this, getString(R.string.event_updated), Toast.LENGTH_LONG).show()
                    popBackStackFragment()
                }
                is Resource.Error -> {
                    Toast.makeText(this, getString(R.string.error_event_update), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkLocationPermission(){
        when {
            //Permissions are granted
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED) -> {
                Log.d(TAG, "ACCESS_FINE_LOCATION permission is granted")
            }
            //Permissions are not granted and the dialog in order to request them is not showed
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d(TAG, "ACCESS_FINE_LOCATION permission is not granted")
            }
            //Permissions are not granted and the dialog in order to request them can be showed
            else -> {
                // Request the storage permission
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.ACCESS_FINE_LOCATION_PERMISSIONS_CODE
                )
            }
        }
    }

    // Overrided function that is executed after a permission requests
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.ACCESS_FINE_LOCATION_PERMISSIONS_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted.
                    Log.d(TAG, "ACCESS_FINE_LOCATION permission is granted")
                } else {
                    Log.d(TAG, "ACCESS_FINE_LOCATION permission is not granted")
                }
                return
            }
        }
    }

    override fun popBackStackFragment(){
        currentNavController?.value?.popBackStack()
    }


    override fun tripSelected(id: String) {
        val trip = mainViewModel.getTripById(id)
        if(trip != null){
            val bundle = bundleOf()
            bundle.putParcelable(Constants.BUNDLE_TRIP, trip)
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_tripsFragment_to_homeTripFragment, bundle)
        }else{
            Toast.makeText(this, R.string.error_load_trip, Toast.LENGTH_LONG).show()
        }

    }

    override fun createTrip() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_tripsFragment_to_createTripFragment)
    }

    override fun userConfigSelected() {

    }

    override fun createEvent(id: String) {
        val trip = mainViewModel.getTripById(id)
        if(trip != null){
            val bundle = bundleOf()
            bundle.putParcelable(Constants.BUNDLE_TRIP, trip)
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_eventsFragment_to_createEventFragment, bundle)
        }else{
            Toast.makeText(this, R.string.error_load_trip, Toast.LENGTH_LONG).show()
        }
    }

    override fun createEventFromHomeFragment(id: String) {
        val trip = mainViewModel.getTripById(id)
        if(trip != null){
            val bundle = bundleOf()
            bundle.putParcelable(Constants.BUNDLE_TRIP, trip)
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_homeTripFragment_to_createEventFragment, bundle)
        }else{
            Toast.makeText(this, R.string.error_load_trip, Toast.LENGTH_LONG).show()
        }
    }

    override fun showEventsTrip(id: String) {
        val trip = mainViewModel.getTripById(id)
        if(trip != null){
            val bundle = bundleOf()
            bundle.putParcelable(Constants.BUNDLE_TRIP, trip)
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_homeTripFragment_to_eventsFragment, bundle)
        }else{
            Toast.makeText(this, R.string.error_load_trip, Toast.LENGTH_LONG).show()
        }
    }

    override fun showEventsMap(id: String) {
        val trip = mainViewModel.getTripById(id)
        if(trip != null){
            val bundle = bundleOf()
            bundle.putParcelable(Constants.BUNDLE_TRIP, trip)
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_homeTripFragment_to_eventsMapFragment, bundle)
        }else{
            Toast.makeText(this, R.string.error_load_trip, Toast.LENGTH_LONG).show()
        }
    }

    override fun showExpenses(id: String) {
        val trip = mainViewModel.getTripById(id)
        if(trip != null){
            val bundle = bundleOf()
            bundle.putParcelable(Constants.BUNDLE_TRIP, trip)
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_homeTripFragment_to_expensesFragment, bundle)
        }else{
            Toast.makeText(this, R.string.error_load_trip, Toast.LENGTH_LONG).show()
        }
    }

    override fun rateTrip(id: String) {
        val trip = mainViewModel.getTripById(id)
        if(trip != null){
            var dialog = RateDialogFragment(trip)
            dialog.show(supportFragmentManager, "rateDialog")
        }else{
            Toast.makeText(this, R.string.error_load_trip, Toast.LENGTH_LONG).show()
        }
    }

    override fun eventSelected(idTrip: String, idEvent: String) {
        this.eventSelected(idTrip, idEvent, R.id.action_eventsFragment_to_eventDetailsFragment)
    }

    override fun eventSelectedFromExpenses(idTrip: String, idEvent: String) {
        this.eventSelected(idTrip, idEvent, R.id.action_expensesFragment_to_eventDetailsFragment)
    }

    private fun eventSelected(idTrip: String, idEvent: String, idAction: Int){
        val trip = mainViewModel.getTripById(idTrip)
        val event = mainViewModel.getEventById(idEvent, idTrip)
        if(trip != null && event != null){
            val bundle = bundleOf()
            bundle.putParcelable(Constants.BUNDLE_TRIP, trip)
            bundle.putParcelable(Constants.BUNDLE_EVENT, event)
            findNavController(R.id.nav_host_fragment).navigate(idAction, bundle)
        }else{
            Toast.makeText(this, R.string.error_load_event, Toast.LENGTH_LONG).show()
        }
    }

    override fun editEvent(trip: Trip, event: Event) {
        val bundle = bundleOf()
        bundle.putParcelable(Constants.BUNDLE_TRIP, trip)
        bundle.putParcelable(Constants.BUNDLE_EVENT, event)
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_eventDetailsFragment_to_editEventFragment, bundle)
    }

    override fun showEventLocation(event: Event) {
        val bundle = bundleOf()
        bundle.putParcelable(Constants.BUNDLE_EVENT, event)
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_eventDetailsFragment_to_eventMapFragment, bundle)
    }

    //Method that signs out the current user, finishes the activity and start the LoginActivity
    override fun signOutUser() {
        //Sign out user from Firebase
        firebaseAuth.signOut()

        // If there is a new log in, it needs to download again the data
        Storage.clearData()
        //Open login activity
        val intent = Intent(this, LoginActivity::class.java)
        // Flags in order to remove from the stack this activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun showAccountInformation() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_profileFragment_to_accountInformationFragment)
    }

    override fun showContactDetails() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_profileFragment_to_contactFragment)
    }

}