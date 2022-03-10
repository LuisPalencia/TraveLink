package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class RateDialogFragment(private val trip: Trip): DialogFragment() {

    private val TAG = "RateDialogFragment"
    private lateinit var rootView: View
    private lateinit var ratingBar: RatingBar
    private lateinit var textViewRating: TextView

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_dialog_rate, container, false)

        setUpView()

        return rootView
    }

    private fun setUpView(){
        this.ratingBar = rootView.findViewById(R.id.ratingBar)
        this.textViewRating = rootView.findViewById(R.id.textViewRating)

        this.ratingBar.rating = trip.rating.toFloat()

        this.ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                Log.d(TAG, "$rating")

                when(rating){
                    in 0.0..1.0 -> {
                        this.textViewRating.text = getString(R.string.very_bad)
                    }
                    in 1.0..2.0 -> {
                        this.textViewRating.text = getString(R.string.not_good)
                    }
                    in 2.0..3.0 -> {
                        this.textViewRating.text = getString(R.string.quite_ok)
                    }
                    in 3.0..4.0 -> {
                        this.textViewRating.text = getString(R.string.very_good)
                    }
                    in 4.0..5.0 -> {
                        this.textViewRating.text = getString(R.string.excellent)
                    }
                }
            }

        rootView.findViewById<Button>(R.id.btnSendRating).setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.rateTrip(ratingBar.rating.toDouble(), this@RateDialogFragment.trip)
            }
            this.dismiss()
        }

        rootView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            this.dismiss()
        }
    }
}