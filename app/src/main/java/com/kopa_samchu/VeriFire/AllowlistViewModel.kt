package com.kopa_samchu.VeriFire

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllowlistViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: AllowlistDao
    val allAllowlistItems: LiveData<List<AllowlistItem>>

    init {
        val database = AppDatabase.getDatabase(application)
        dao = database.allowlistDao()
        allAllowlistItems = dao.getAllItems()
    }

    fun insert(item: AllowlistItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    fun delete(item: AllowlistItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(item)
        }
    }
}
    