package com.luispalenciadelcampo.travelink.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class RateEventDialogFragment(private val event: Event, private val tripId: String): DialogFragment() {

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

        this.ratingBar.rating = event.rating.toFloat()
        setRatingMessage(this.ratingBar.rating)

        this.ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                setRatingMessage(rating)
            }

        rootView.findViewById<ImageButton>(R.id.btnSendRating).setOnClickListener {
            lifecycleScope.launch {
                val rating = ratingBar.rating.toDouble()
                this@RateEventDialogFragment.event.rating = rating
                mainViewModel.rateEvent(rating, this@RateEventDialogFragment.event.id, this@RateEventDialogFragment.tripId)
            }
            this.dismiss()
        }

        rootView.findViewById<ImageButton>(R.id.btnCancel).setOnClickListener {
            this.dismiss()
        }

        rootView.findViewById<TextView>(R.id.textViewDescription).setText(R.string.rate_this_event)

    }

    private fun setRatingMessage(rating: Float){
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
}