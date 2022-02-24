package com.luispalenciadelcampo.travelink.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.data.dto.TripPlace

class CitiesAdapter (
    private val context: Context,
    //private val mainViewModel: MainViewModel
): RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    private var TAG = "TripsAdapter"
    private lateinit var mListener : OnItemClickListener
    private var data = mutableListOf<TripPlace>()

    interface OnItemClickListener {
        fun onRemoveCityClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    fun setCitiesList(cities: MutableList<TripPlace>){
        data = cities
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {

        var textViewCityName: TextView
        var btnRemoveCity: ImageView

        init {
            textViewCityName = view.findViewById(R.id.textViewCityName)
            btnRemoveCity = view.findViewById(R.id.btnRemoveCity)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_city, viewGroup, false)

        return ViewHolder(view, mListener)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textViewCityName.text = data[position].name

        viewHolder.itemView.setOnClickListener {
            mListener.onRemoveCityClick(position)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}