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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.data.dto.User
import com.luispalenciadelcampo.travelink.databinding.FragmentAccountInformationBinding
import com.luispalenciadelcampo.travelink.databinding.FragmentProfileBinding
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.luispalenciadelcampo.travelink.utils.PatternsAccount
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class AccountInformationFragment : Fragment() {

    private val TAG = "AccountInformationFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var user: User

    private var _binding: FragmentAccountInformationBinding? = null
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
        _binding = FragmentAccountInformationBinding.inflate(inflater, container, false)
        rootView = binding.root

        if(mainViewModel.user.value != null){
            this.user = mainViewModel.user.value!!

            setView()
            setButtons()
            setObserver()
        }else{
            Log.e(TAG, "AccountInformationFragment received a null User object")
            supportFragmentManager.popBackStackFragment()
            return null
        }

        return rootView
    }

    private fun setView(){
        binding.editTextEmail.setText(FirebaseAuth.getInstance().currentUser?.email ?: "")
        binding.editTextFirstName.setText(this.user.name)
        binding.editTextLastName.setText(this.user.lastname)
        binding.editTextBirthday.setText(GenericFunctions.dateToString(this.user.birthday))

        binding.loadingAnimation.isVisible = false
    }

    private fun setButtons(){
        binding.editTextBirthday.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            builder.setTitleText(R.string.select_your_birthday)

            val materialDatePicker = builder.build()

            materialDatePicker.show(parentFragmentManager, "dateRangePicker")

            materialDatePicker.addOnPositiveButtonClickListener {
                binding.editTextBirthday.setText("${GenericFunctions.dateToString(Date(it))}")
            }
        }

        binding.btnUpdateInfo.setOnClickListener {
            val firstname = binding.editTextFirstName.text.toString()
            val lastname = binding.editTextLastName.text.toString()
            val birthday = binding.editTextBirthday.text.toString()

            if(firstname.isEmpty() ||
                lastname.isEmpty() ||
                birthday.isEmpty()
            ){ // There are errors in the data introduced
                Snackbar.make(it, getString(R.string.error_in_data_account), Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // All data is correct, proceed to update user info
            val newUserInfo = User(this.user.uuid, firstname, lastname, GenericFunctions.stringToDate(birthday))

            lifecycleScope.launch {
                mainViewModel.updateUserInfo(newUserInfo)
            }
        }
    }

    private fun setObserver(){
        mainViewModel.updateInfoUser.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.loadingAnimation.isVisible = false
                    Toast.makeText(this.requireContext(), R.string.success_update_user_info, Toast.LENGTH_LONG).show()
                    supportFragmentManager.popBackStackFragment()
                }
                is Resource.Error -> {
                    Toast.makeText(this.requireContext(), R.string.error_update_user_info, Toast.LENGTH_LONG).show()
                    binding.loadingAnimation.isVisible = false
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