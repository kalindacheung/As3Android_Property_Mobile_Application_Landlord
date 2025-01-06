package com.example.landlordapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.landlordapp.adapters.PropertyAdapter
import com.example.landlordapp.clickinterface.ClickDetectorInterface
import com.example.landlordapp.databinding.ActivityViewListingsBinding
import com.example.landlordapp.models.Property
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore


class ViewListingsActivity : AppCompatActivity(), ClickDetectorInterface {

    // ------------------------------------------------------
    // Activity Setup
    // ------------------------------------------------------
    lateinit var binding: ActivityViewListingsBinding
    lateinit var adapter: PropertyAdapter
    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    var dataToDisplay:MutableList<Property> = mutableListOf(
        Property("", "", "", 0.0,0, true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // ------------------------------------------------------
        // Adapter Setup and recyclerview configuration
        // ------------------------------------------------------
        adapter = PropertyAdapter(dataToDisplay,this )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )

        // insert data into the recyclerview
        insertPropertyinfo()

    }


    // ------------------------------------------------------
    // Helper functions
    // ------------------------------------------------------

    override fun onResume() {
        super.onResume()
        insertPropertyinfo()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_go_back -> {
//                intent= Intent(this, MainActivity::class.java)
//                startActivity(intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun deleteRow(position: Int) {
        val property = dataToDisplay[position]
        db.collection("landlordProperties")
            .document(property.id)
            .delete()
            .addOnSuccessListener {
                dataToDisplay.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
            .addOnFailureListener {
                    e ->
                Log.w("TESTING", "ERROR deleting document", e)
            }
    }

    override fun updateRow(position: Int) {
        val property = dataToDisplay[position]
        var intent = Intent(this,CreateListingActivity::class.java)
        intent.putExtra("EXTRA_IS_ADDING",false)
        intent.putExtra("EXTRA_PROPERTY_ID",property.id)
        startActivity(intent)
    }



    // ------------------------------------------------------
    // Helper functions for insert data into the recyclerview
    // ------------------------------------------------------
    fun insertPropertyinfo(){
        db.collection("landlordProperties")
            .whereEqualTo("owner", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                    results: QuerySnapshot ->
                val LandlordListFromDB:MutableList<Property> = mutableListOf()
                for (document: QueryDocumentSnapshot in results) {
                    val LandlordData:Property = document.toObject(Property::class.java)
                    LandlordListFromDB.add(LandlordData)
                }
                dataToDisplay.clear()
                dataToDisplay.addAll(LandlordListFromDB)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                    e ->
                Log.w("TESTING", "ERROR deleting document", e)
            }
    }




}

