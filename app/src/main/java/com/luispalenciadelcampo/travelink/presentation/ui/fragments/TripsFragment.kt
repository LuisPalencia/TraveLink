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
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.databinding.FragmentTripsBinding
import com.luispalenciadelcampo.travelink.presentation.ui.activities.MainActivity
import com.luispalenciadelcampo.travelink.presentation.interfaces.SupportFragmentManager
import com.luispalenciadelcampo.travelink.presentation.ui.adapters.TripsAdapter
import com.luispalenciadelcampo.travelink.presentation.ui.adapters.TripsViewPagerAdapter
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import com.luispalenciadelcampo.travelink.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.IOException


class TripsFragment : Fragment() {

    private val TAG = "TripsFragment"
    private lateinit var supportFragmentManager: SupportFragmentManager
    private lateinit var rootView: View

    private val mainViewModel: MainViewModel by activityViewModels()

    // Adapter
    private lateinit var adapter: TripsViewPagerAdapter

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

        setViewPagerAdapter()
        getInitialData()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setViewPagerAdapter(){
        this.adapter = TripsViewPagerAdapter(parentFragmentManager, requireActivity().lifecycle)
        binding.viewPagerTrips.adapter = this.adapter

        val tabNames = arrayOf(getString(R.string.current_trips), getString(R.string.past_trips))

        TabLayoutMediator(binding.tabLayout, binding.viewPagerTrips) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getInitialData(){
        lifecycleScope.launch {
            mainViewModel.getTrips(FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }




}