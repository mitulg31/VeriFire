package com.kopa_samchu.VeriFire

interface OnMessageClickListener {
    fun onDeleteClicked(message: BlockedMessage)
    fun onMessageClicked(message: BlockedMessage)
}
