package com.example.landlordapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.landlordapp.R
import com.example.landlordapp.clickinterface.ClickDetectorInterface
import com.example.landlordapp.databinding.ItemPropertyBinding
import com.example.landlordapp.models.Property


class PropertyAdapter
    (val myItems:MutableList<Property>,
     val clickInterface: ClickDetectorInterface)
    : RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPropertyBinding) : RecyclerView.ViewHolder (binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return myItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val myProperty: Property = this.myItems[position]
//        holder.binding.propertyImage.setImageResource(myProperty.imageUrl.toString())

        Glide.with(holder.binding.propertyImage.context)
            .load(myProperty.imageUrl)
            .placeholder(R.drawable.ic_launcher_background) // Optional placeholder
            .error(R.drawable.ic_launcher_background) // Optional error image
            .into(holder.binding.propertyImage)

        holder.binding.propertyAddress.text = myProperty.propertyAddress
        holder.binding.propertyPrice.text = "Monthy Rental Price: ${myProperty.monthlyRentalPrice.toString()}"
        holder.binding.propertyBedrooms.text = "Number of Bedrooms: ${myProperty.numberOfBedRooms.toString()}"
        if(myProperty.status == true){
            holder.binding.propertyStatus.text= "Status: Rented"
            holder.binding.propertyStatus.setTextColor(android.graphics.Color.GREEN)
        } else{
            holder.binding.propertyStatus.text= "Status: Available for rent"
            holder.binding.propertyStatus.setTextColor(android.graphics.Color.RED)
        }



        holder.binding.btnDelete.setOnClickListener {
            clickInterface.deleteRow(position)
        }
        holder.binding.btnUpdate.setOnClickListener {
            clickInterface.updateRow(position)
        }



    }


}
