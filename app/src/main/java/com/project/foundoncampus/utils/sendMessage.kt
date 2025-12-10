package com.project.foundoncampus.utils

import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

fun sendMessage(db: FirebaseFirestore, senderEmail: String, message: String) {
    val newMessage = hashMapOf(
        "senderEmail" to senderEmail,
        "message" to message,
        "timestamp" to Date()
    )

    db.collection("group_chat") // ✅ fixed collection name
        .add(newMessage)
        .addOnSuccessListener { /* You can log or show toast */ }
        .addOnFailureListener { e -> e.printStackTrace() }
}
