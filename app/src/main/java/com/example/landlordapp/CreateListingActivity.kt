package com.example.landlordapp

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.landlordapp.databinding.ActivityCreateListingBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.Locale


class CreateListingActivity : AppCompatActivity() {
    
    // ------------------------------------------------------
    // Activity Setup
    // ------------------------------------------------------
    private lateinit var binding: ActivityCreateListingBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val db = Firebase.firestore
    lateinit var auth: FirebaseAuth

    // by default, assume this screen is used for adding landlord property to the db
    var isAdding = true
    var currPropertyId:String? = null     // if you are adding, this should not exist

    // Request for Location Permission
    val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts
        .RequestPermission()) {
            permissionGranted:Boolean ->
        if (permissionGranted == true) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission is required to fetch current location.", Toast.LENGTH_SHORT).show()
        }
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // get data from previous screen
        if (intent != null) {
            this.isAdding = intent.getBooleanExtra("EXTRA_IS_ADDING", true)
            if (this.isAdding == false) {
                binding.tvTitle.text ="Update the Property"
                this.currPropertyId = intent.getStringExtra("EXTRA_PROPERTY_ID")
                db.collection("landlordProperties")
                    .document(this.currPropertyId!!)
                    .get()
                    .addOnSuccessListener {
                       binding.etPropertyAddress.setText(it.get("propertyAddress").toString())
                        binding.etImageUrl.setText(it.get("imageUrl").toString())
                        binding.etRentalPrice.setText(it.get("monthlyRentalPrice").toString())
                        binding.etBedrooms.setText(it.get("numberOfBedRooms").toString())
                        binding.cbStatus.isChecked = it.get("status").toString().toBoolean()
                    }
                    .addOnFailureListener {
                            e ->
                        Log.w("TESTING", "ERROR deleting document", e)
                    }
            }
        }


        // ------------------------------------------------------
        // Handle "Use Current Location" button
        // ------------------------------------------------------
        binding.btnUseCurrentLocation.setOnClickListener {
            requestLocationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // ------------------------------------------------------
        // Handle "Submit Listing" button
        // ------------------------------------------------------
        binding.btnSubmitListing.setOnClickListener {

            val address = binding.etPropertyAddress.text.toString()
            val imageUrl = binding.etImageUrl.text.toString()
            val price = binding.etRentalPrice.text.toString().toDoubleOrNull()
            val bedrooms = binding.etBedrooms.text.toString().toIntOrNull()
            val status = binding.cbStatus.isChecked

            if (address.isNotEmpty() && imageUrl.isNotEmpty() && price != null && bedrooms != null) {
                // Geocode address to get latitude and longitude
                val (latitude, longitude) = getCoordinatesFromAddress(address)

                if (latitude != null && longitude != null) {
                    val data: MutableMap<String, Any> = HashMap()
                    data["propertyAddress"] = address
                    data["latitude"] = latitude
                    data["longitude"] = longitude
                    data["imageUrl"] = imageUrl
                    data["monthlyRentalPrice"] = price
                    data["numberOfBedRooms"] = bedrooms
                    data["status"] = status
                    data["owner"] = auth.currentUser!!.uid



                    // if it is Update screen, update the property
                    if(this.isAdding == false){
                        db.collection("landlordProperties")
                            .document(this.currPropertyId!!)
                            .set(data)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Listing updated successfully!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to updated listing: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                    }else{
                        // if it is Create screen, create the property
                        db.collection("landlordProperties")
                            .add(data)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Listing submitted successfully!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to submit listing: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }


                } else {
                    //Checked if the address is invalid
                    Toast.makeText(this, "Unable to find coordinates for the address.", Toast.LENGTH_SHORT).show()
                }
            } else {
                //Checked all fields are filled
                Toast.makeText(this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
            }
        }



    }


    // ------------------------------------------------------
    // Helper functions for Action Bar
    // ------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_go_back -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    // ------------------------------------------------------
    // Helper functions for Geocode address to get latitude and longitude
    // ------------------------------------------------------
    private fun getCoordinatesFromAddress(address: String): Pair<Double?, Double?> {
        //Initialize Geocoder and convert address into latitude and longitude
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            //The getFromLocationName method is used to retrieve a list of possible locations corresponding to the given address.
            val addressList = geocoder.getFromLocationName(address, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                //If the result is not empty, the first location in the list is used to extract latitude and longitude.
                val location = addressList[0]
                Pair(location.latitude, location.longitude)
            } else {
                //If no result is found or an exception occurs, null values are returned for both coordinates.
                Pair(null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
        }
    }


    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Use Geocoder to convert location into a human-readable address
                val geocoder = Geocoder(this, Locale.getDefault())
                val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    // Extract the full address from the first result and display it in the EditText.
                    val address = addressList[0].getAddressLine(0)
                    binding.etPropertyAddress.setText(address)
                } else {
                    Toast.makeText(this, "Unable to fetch address.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Unable to get current location.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


}


