package com.himanshu.semhub.ui.viewmodel.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.chat.Chat
import com.himanshu.semhub.data.model.chat.Message
import com.himanshu.semhub.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(val chatRepository: ChatRepository) : ViewModel() {

    private val conversation = StringBuilder()
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    private val _messageState = MutableStateFlow<MessageState>(MessageState.Idle)
    val messageState: StateFlow<MessageState> = _messageState.asStateFlow()

    init {
        viewModelScope.launch {
            _messageState.value = MessageState.Loading
            val chatHistory = chatRepository.getChat()
            if(chatHistory != null){
                _messages.value = chatHistory.conversation
            }
            else{
                _messages.value = emptyList()
            }
            _messageState.value = MessageState.Idle
        }
    }


    fun sendMessage(message: String) {
        _messages.value = _messages.value.toMutableList().apply {
            add(Message(content = message, time = System.currentTimeMillis(), isUser = true))
        }  // 🔥 Now `_messages` gets a NEW instance!

        conversation.append("$message. ")
        _messageState.value = MessageState.Typing

        viewModelScope.launch {
            try {
                val response = chatRepository.sendMessage(conversation.toString())

                _messages.value = _messages.value.toMutableList().apply {
                    add(Message(content = response, time = System.currentTimeMillis(), isUser = false))
                }  // 🔥 Again, new instance ensures Compose detects the update

                conversation.append("$response. ")
                _messageState.value = MessageState.Idle
            } catch (e: Exception) {
                _messageState.value = MessageState.Error(e.localizedMessage)
            }
        }
    }


    fun saveChat() {
        val chat = Chat(conversation = messages.value)
        viewModelScope.launch {
            chatRepository.saveChat(chat)
            Log.d(TAG, "Chat saved")
        }
    }

    companion object{
        const val TAG = "ChatViewModel"
    }
}


sealed class MessageState{
    data object Idle : MessageState()
    data object Typing : MessageState()
    data object Loading : MessageState()
    data class Error(val message: String = "Unknown error") : MessageState()
}