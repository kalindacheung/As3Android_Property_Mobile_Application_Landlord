package com.example.landlordapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.landlordapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth


        // ------------------------------------------------------
        // Link to CreateListingActivity
        // ------------------------------------------------------
        findViewById<Button>(R.id.btnCreateListing).setOnClickListener {
            val intent = Intent(this, CreateListingActivity::class.java)
            startActivity(intent)
        }

        // ------------------------------------------------------
        // Link to ViewListingsActivity
        // ------------------------------------------------------
        findViewById<Button>(R.id.btnViewListings).setOnClickListener {
            val intent = Intent(this, ViewListingsActivity::class.java)
            startActivity(intent)
        }
        Log.d("TESTING", "Current user: ${auth.currentUser?.uid ?: "No user logged in"}")
    }


    // ------------------------------------------------------
    // ActionBar info
    // ------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.settings_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_go_back -> {
                auth.signOut()
                Toast.makeText(this, "Logout successful!", Toast.LENGTH_SHORT).show()
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




}
