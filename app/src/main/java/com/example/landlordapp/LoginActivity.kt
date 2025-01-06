package com.example.landlordapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.landlordapp.databinding.ActivityLoginBinding
import com.example.landlordapp.models.landOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    // ------------------------------------------------------
    // Activity Setup
    // ------------------------------------------------------
    private lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // ------------------------------------------------------
        // Set up login button click listener
        // ------------------------------------------------------
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

        // ------------------------------------------------------
        // Clear the email and password when onResume() and Check if user is logged in
        // ------------------------------------------------------
        override fun onResume() {
                super.onResume()
                Log.d("TESTING", "Current user: ${auth.currentUser?.uid ?: "No user logged in"}")
                binding.etEmail.setText("")
                binding.etPassword.setText("")
        }

        // ------------------------------------------------------
        // Login User function
        // ------------------------------------------------------
        private fun loginUser(email: String, password: String) {
            auth.signInWithEmailAndPassword(email, password)
              .addOnCompleteListener { permission ->
                    if (permission.isSuccessful) {
                       db.collection("landOwner")
                         .document(this.auth.currentUser!!.uid)
                         .get()
                         .addOnSuccessListener {
                                document: DocumentSnapshot ->
                            val profile: landOwner? = document.toObject(landOwner::class.java)
                            //Check the user type
                            if (profile?.isLandOwner == true) {
                                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "Access denied: Not a landowner.", Toast.LENGTH_SHORT).show()
                            }
                         }.addOnFailureListener {
                                exception ->
                            Log.w("TESTING", "Error getting documents.", exception)
                        }
                    } else {
                    Toast.makeText(this, "Login failed: ${permission.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
              }
        }




}