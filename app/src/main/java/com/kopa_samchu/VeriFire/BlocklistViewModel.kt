package com.kopa_samchu.VeriFire

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlocklistViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: BlocklistDao
    val allBlocklistItems: LiveData<List<BlocklistItem>>

    init {
        val database = AppDatabase.getDatabase(application)
        dao = database.blocklistDao()
        allBlocklistItems = dao.getAllItems()
    }

    fun insert(item: BlocklistItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    fun delete(item: BlocklistItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(item)
        }
    }
}
    