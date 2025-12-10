package com.project.foundoncampus.model

import java.util.Date

data class ChatMessage(
    val sender: String,
    val message: String,
    val timestamp: Date
)
