package com.project.foundoncampus.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.foundoncampus.model.ListingItem
import com.project.foundoncampus.utils.FileUtils
import kotlinx.coroutines.launch

class CreateViewModel(application: Application) : AndroidViewModel(application) {

    fun saveListing(item: ListingItem) {
        viewModelScope.launch {
            FileUtils.saveItem(getApplication(), item)
        }
    }
}
