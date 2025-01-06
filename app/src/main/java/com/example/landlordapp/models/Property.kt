package com.example.landlordapp.models

import com.google.firebase.firestore.DocumentId

data class Property(
    @DocumentId
    var id: String = "",
    var propertyAddress: String = "",
    var imageUrl: String = "https://via.placeholder.com/250",
    var monthlyRentalPrice: Double = 0.0,
    var numberOfBedRooms: Int = 0,
    var status: Boolean =true
)

