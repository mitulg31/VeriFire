package com.kopa_samchu.VeriFire

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlockedMessagesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: BlockedMessageDao
    val allBlockedMessages: LiveData<List<BlockedMessage>>

    init {
        val database = AppDatabase.getDatabase(application)
        dao = database.blockedMessageDao()
        allBlockedMessages = dao.getAllMessages()
    }

    fun delete(message: BlockedMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(message)
        }
    }
}
