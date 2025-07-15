package com.project.foundoncampus.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.foundoncampus.model.ListingItem
import com.project.foundoncampus.model.User
import java.io.File

object FileUtils {
    private const val LISTING_FILE_NAME = "data.json"
    private const val USER_FILE_NAME = "users.json"
    private val gson = Gson()

    // --- Listing methods ---
    fun saveItem(context: Context, item: ListingItem) {
        val file = File(context.filesDir, LISTING_FILE_NAME)
        val items = loadItems(context).toMutableList()
        items.add(item)
        file.writeText(gson.toJson(items))
    }

    fun loadItems(context: Context): List<ListingItem> {
        val file = File(context.filesDir, LISTING_FILE_NAME)
        if (!file.exists()) return emptyList()
        val listType = object : TypeToken<List<ListingItem>>() {}.type
        return gson.fromJson(file.readText(), listType)
    }

    // --- User methods ---
    fun saveUser(context: Context, user: User) {
        val file = File(context.filesDir, USER_FILE_NAME)
        val users = loadUsers(context).toMutableList()
        users.add(user)
        file.writeText(gson.toJson(users))
    }

    fun loadUsers(context: Context): List<User> {
        val file = File(context.filesDir, USER_FILE_NAME)
        if (!file.exists()) return emptyList()
        val listType = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(file.readText(), listType)
    }
}
