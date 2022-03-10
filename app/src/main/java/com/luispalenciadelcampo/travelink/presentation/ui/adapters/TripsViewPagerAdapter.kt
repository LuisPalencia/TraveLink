package com.luispalenciadelcampo.travelink.presentation.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.luispalenciadelcampo.travelink.presentation.ui.fragments.TripListFragment
import com.luispalenciadelcampo.travelink.presentation.ui.fragments.TripsType

class TripsViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TripListFragment(TripsType.CURRENT_TRIP)
            else -> TripListFragment(TripsType.PAST_TRIP)
        }
    }
}