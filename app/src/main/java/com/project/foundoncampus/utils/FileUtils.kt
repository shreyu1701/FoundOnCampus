package com.project.foundoncampus.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.foundoncampus.model.ListingItem
import java.io.File

object FileUtils {
    private const val FILE_NAME = "data.json"
    private val gson = Gson()

    fun saveItem(context: Context, item: ListingItem) {
        val file = File(context.filesDir, FILE_NAME)
        val items = loadItems(context).toMutableList()
        items.add(item)
        file.writeText(gson.toJson(items))
    }

    fun loadItems(context: Context): List<ListingItem> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        val listType = object : TypeToken<List<ListingItem>>() {}.type
        return gson.fromJson(file.readText(), listType)
    }
}