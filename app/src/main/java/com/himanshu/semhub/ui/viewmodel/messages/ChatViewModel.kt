package com.himanshu.semhub.ui.viewmodel.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.message.Message
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


    fun sendMessage(message: String) {
        _messages.value += Message(message, System.currentTimeMillis(), true)
        conversation.append("message: $message. ")
        _messageState.value = MessageState.Typing
        viewModelScope.launch {
            try {
                val response = chatRepository.sendMessage(conversation.toString())
                _messages.value += Message(response, System.currentTimeMillis(), false)
                conversation.append("response: $response. ")
                _messageState.value = MessageState.Idle
            } catch (e: Exception) {
                _messageState.value = MessageState.Error(e.localizedMessage)
            }
        }
    }
}


sealed class MessageState{
    data object Idle : MessageState()
    data object Typing : MessageState()
    data class Error(val message: String = "Unknown error") : MessageState()
}