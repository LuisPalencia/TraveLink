package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.data.dto.TripPlace
import com.luispalenciadelcampo.travelink.databinding.FragmentProfileBinding
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.luispalenciadelcampo.travelink.utils.Resource
import com.luispalenciadelcampo.travelink.utils.observeOnce
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException


class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        rootView = binding.root

        setButtons()
        setView()
        setObserver()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Method that sets the logic of the buttons
    private fun setButtons(){
        // Button that navigates to the Account information fragment
        binding.btnProfileConfig.setOnClickListener {
            supportFragmentManager.showAccountInformation()
        }

        // Button that allows user to select a profile image from the gallery
        binding.btnSelectProfileImage.setOnClickListener {
            checkReadExternalStoragePermission()
        }

        // Button that navigates to the Contact fragment
        binding.btnContact.setOnClickListener {
            supportFragmentManager.showContactDetails()
        }

        // Button that logoff the user
        binding.btnLogoff.setOnClickListener {
            supportFragmentManager.signOutUser()
        }

        // Button that allows user to select a profile image from the gallery
        binding.imgProfile.setOnClickListener {
            checkReadExternalStoragePermission()
        }
    }

    // Method that sets up the view
    private fun setView(){
        // Hide the loading animation
        binding.loadingAnimation.isVisible = false
        // Set the hello text
        binding.txtHelloUser.text = "${getString(R.string.hello)}, ${mainViewModel.user.value?.name}"

        // Set the profile image
        if(mainViewModel.user.value?.profileImageUrl?.isNotEmpty() == true){
            Glide.with(this.requireContext())
                .load(mainViewModel.user.value?.profileImageUrl)
                .into(binding.imgProfile)
        }
    }

    // Method that set ups the observer for the image profile upload
    private fun setObserver(){
        mainViewModel.uploadProfileImageStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> { // The profile image has been updated
                    setView()
                    Snackbar.make(binding.constraintLayout, getString(R.string.success_updating_profile_image), Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> { // An error ocurred when trying to set up the image
                    binding.loadingAnimation.isVisible = false
                    Snackbar.make(binding.constraintLayout, getString(R.string.error_updating_profile_image), Snackbar.LENGTH_SHORT).show()
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


    @SuppressLint("MissingPermission")
    private fun checkReadExternalStoragePermission(){
        try{
            when {
                //Permissions are granted
                ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED) -> {
                    takeImageFromGallery()
                }
                //Permissions are not granted and the dialog in order to request them is not showed
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    Snackbar.make(binding.constraintLayout, getString(R.string.error_permissions_gallery_settings), Snackbar.LENGTH_LONG).show()
                }
                //Permissions are not granted and the dialog in order to request them can be showed
                else -> {
                    // Request the permission
                    requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }catch (e: SecurityException){
            Log.d(TAG, e.toString())
        }
    }

    private var requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if (isGranted) {
            takeImageFromGallery()
        } else {
            // Failed pass
            Snackbar.make(binding.constraintLayout, getString(R.string.error_permissions_gallery), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun takeImageFromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncherGalleryImage.launch(galleryIntent)
    }

    private var resultLauncherGalleryImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // There are no request codes

        when(result.resultCode){
            Activity.RESULT_OK -> {
                result.data?.data?.let {
                    val uriImage = it
                    val bitmapCompressed = GenericFunctions.getBitmapCompressedFromUri(uriImage, context)

                    lifecycleScope.launch {
                        mainViewModel.uploadProfileImage(FirebaseAuth.getInstance().uid ?: "", bitmapCompressed)
                    }
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                result.data?.let {
                    val status = Autocomplete.getStatusFromIntent(it)
                    //status.statusMessage?.let { message -> Log.i(TAG, message) }
                    status.statusMessage?.let { message -> Log.d(TAG, "Error: $message") }
                    Snackbar.make(binding.constraintLayout, getString(R.string.error_updating_profile_image), Snackbar.LENGTH_LONG).show()
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.i(TAG, "User cancelled the action")
            }
        }
    }


}