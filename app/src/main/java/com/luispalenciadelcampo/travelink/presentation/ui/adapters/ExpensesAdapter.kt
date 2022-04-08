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
import com.luispalenciadelcampo.travelink.data.dto.Event

class ExpensesAdapter(
    private val context: Context,
    //private val mainViewModel: MainViewModel
): RecyclerView.Adapter<ExpensesAdapter.ViewHolder>() {

    private var TAG = "ExpensesAdapter"
    private lateinit var mListener : OnItemClickListener
    private var data = mutableListOf<Event>()

    interface OnItemClickListener {
        fun onItemClick(event: Event)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    fun setExpensesList(trips: MutableList<Event>){
        data = trips
    }



    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(view) {

        var imageViewExpense: ImageView
        var textViewNameExpense: TextView
        var textViewQuantityExpense: TextView

        init {
            imageViewExpense = view.findViewById(R.id.imageViewExpense)
            textViewNameExpense = view.findViewById(R.id.textViewNameExpense)
            textViewQuantityExpense = view.findViewById(R.id.textViewQuantityExpense)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_expenses_list, viewGroup, false)

        return ViewHolder(view, mListener)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.textViewNameExpense.text = data[position].name
        viewHolder.textViewQuantityExpense.text = "${data[position].price} €"

        if(data[position].imageEvent?.image != null){
            viewHolder.imageViewExpense.setImageBitmap(data[position].imageEvent?.image)
        }else{
            Glide.with(context)
                .load(R.drawable.trip1)
                .into(viewHolder.imageViewExpense)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}