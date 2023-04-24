package com.example.wanderwings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.coroutines.coroutineContext

class RecyclerViewAdapter(private val items: MutableList<String>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private lateinit var selectedItems:BooleanArray


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        val textView: TextView = itemView.findViewById(R.id.text_view)
        val removeButton: Button = itemView.findViewById(R.id.remove_button)
        var isSelected = false
        init {
            itemView.setOnLongClickListener(this)
       }
        override fun onLongClick(v: View?): Boolean {
            isSelected = !isSelected
            itemView.setBackgroundColor(if (isSelected) Color.LTGRAY else Color.WHITE)
            return true
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        selectedItems = BooleanArray(itemCount)
        val item = items[position]
        Log.i("mtag",position.toString()+" "+selectedItems.size)
        holder.textView.text = item
        selectedItems[position] = holder.isSelected
        holder.itemView.setBackgroundColor(if (holder.isSelected) Color.GREEN else Color.WHITE)
        holder.itemView.setOnClickListener {
            if(holder.removeButton.isVisible) {
                holder.removeButton.visibility = View.GONE
            }else{
                holder.removeButton.visibility = View.VISIBLE
            }
        }
        holder.removeButton.setOnClickListener {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size

    }


}
