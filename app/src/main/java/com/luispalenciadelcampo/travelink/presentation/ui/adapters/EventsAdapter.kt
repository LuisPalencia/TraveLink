package com.luispalenciadelcampo.travelink.presentation.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.luispalenciadelcampo.travelink.R
import com.luispalenciadelcampo.travelink.data.dto.Event
import com.luispalenciadelcampo.travelink.data.dto.Trip
import com.luispalenciadelcampo.travelink.databinding.ItemEventListBinding
import com.luispalenciadelcampo.travelink.utils.DataItem
import com.luispalenciadelcampo.travelink.utils.GenericFunctions
import com.luispalenciadelcampo.travelink.utils.TripFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class EventsAdapter(
    private val context: Context,
    private val trip: Trip
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(EventsDiffCallback()) {

    private lateinit var mListener : EventsListener
    private val adapterScope = CoroutineScope(Dispatchers.Default)


    interface EventsListener {
        fun onItemClick(position: Int, idEvent: String)
    }

    fun setEventsListener(listener: EventsListener){
        mListener = listener
    }

    fun addHeaderAndSubmitList(eventsList: MutableList<Event>){
        adapterScope.launch {
            val items = when(eventsList){
                null -> listOf(DataItem.Header("Loading"))
                else -> {
                    val eventListGrouped = eventsList.groupBy { it.day }
                    var finalEventsList = mutableListOf<DataItem>()

                    for(day in eventListGrouped.keys){
                        finalEventsList.add(DataItem.Header(day.toString()))
                        for(event in eventListGrouped.getValue(day)){
                            finalEventsList.add(DataItem.EventItem(event))
                        }
                    }

                    finalEventsList
                }
            }

            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        when (viewholder) {
            is ViewHolder -> {
                val eventItem = getItem(position) as DataItem.EventItem
                // Set data of the view
                viewholder.bind(eventItem.event, context)

                // Set the click listener for the list item
                viewholder.itemView.setOnClickListener {
                    mListener.onItemClick(position, eventItem.id)
                }

            }
            is TextViewHolder -> {
                val dayItem = getItem(position) as DataItem.Header
                viewholder.bind(TripFunctions.getStringForDayPosition(dayItem.idDay.toInt(), trip.startDate))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.EventItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class TextViewHolder(view: View): RecyclerView.ViewHolder(view){

        var textViewDay: TextView

        init {
            textViewDay = view.findViewById(R.id.textViewDay)
        }

        fun bind(day: String) {
            textViewDay.text = day
        }

        companion object{
            fun from(parent: ViewGroup): TextViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header_events_list, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    class ViewHolder private constructor(val binding: ItemEventListBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Event, context: Context) {

            if(item.imageUrl?.isNotEmpty() == true){
                //binding.imageViewEvent.setImageBitmap(item.imageEvent!!.image)
                Glide.with(context)
                    .load(item.imageUrl)
                    .into(binding.imageViewEvent)
            }else{
                Glide.with(context)
                    .load(R.drawable.trip1)
                    .into(binding.imageViewEvent)
            }

            binding.textViewEventName.text = item.name
            binding.textViewEventTime.text = "${GenericFunctions.dateHourToString(item.startTime)} - ${GenericFunctions.dateHourToString(item.endTime)}"
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemEventListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class EventsDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}