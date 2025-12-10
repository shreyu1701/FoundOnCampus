package com.project.foundoncampus.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

fun sendMessage(db: FirebaseFirestore, senderEmail: String, message: String) {
    val newMessage = hashMapOf(
        "senderEmail" to senderEmail,
        "message" to message,
        "timestamp" to Date()
    )

    db.collection("group_chat")
        .add(newMessage)
        .addOnSuccessListener {
            Log.d("FIREBASE_CHAT", "Message sent successfully")
        }
        .addOnFailureListener { e ->
            Log.e("FIREBASE_CHAT", "Failed to send message: ${e.message}")
            e.printStackTrace()
        }
}
