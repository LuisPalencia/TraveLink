package com.luispalenciadelcampo.travelink.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.utils.GenericFunctions

class TripsAdapter(
    private val context: Context,
    //private val mainViewModel: MainViewModel
): RecyclerView.Adapter<TripsAdapter.ViewHolder>() {

    private var TAG = "TripsAdapter"
    private lateinit var mListener : OnItemClickListener
    private var data = mutableListOf<Trip>()

    interface OnItemClickListener {
        fun onItemClick(position: Int, idTrip: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    fun setTripsList(trips: MutableList<Trip>){
        data = trips
    }



    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {

        var imageViewTrip: ImageView
        var textViewTripName: TextView
        var textViewTripDate: TextView

        init {
            imageViewTrip = view.findViewById(R.id.imageViewTrip)
            textViewTripName = view.findViewById(R.id.textViewTripName)
            textViewTripDate = view.findViewById(R.id.textViewTripDate)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_trip_list, viewGroup, false)

        return ViewHolder(view, mListener)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewTripName.text = data[position].name
        viewHolder.textViewTripDate.text = "${GenericFunctions.dateToString(data[position].startDate!!)} - ${GenericFunctions.dateToString(data[position].endDate!!)}"

        if (data[position].imageUrl?.isNotEmpty() == true) {
            Glide.with(context)
                .load(data[position].imageUrl)
                .into(viewHolder.imageViewTrip)
        }else{
            Glide.with(context)
                .load(R.drawable.trip1)
                .into(viewHolder.imageViewTrip)
        }

        viewHolder.itemView.setOnClickListener {
            mListener.onItemClick(position, data[position].id!!)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}