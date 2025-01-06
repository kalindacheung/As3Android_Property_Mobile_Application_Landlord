package com.example.landlordapp.models

import com.google.firebase.firestore.DocumentId

data class landOwner(
    @DocumentId
    var id: String = "",
    @JvmField
    var isLandOwner: Boolean = false,
)
