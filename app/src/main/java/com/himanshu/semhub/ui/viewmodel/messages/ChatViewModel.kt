package com.himanshu.semhub.ui.viewmodel.messages

import androidx.lifecycle.ViewModel
import com.himanshu.semhub.data.model.message.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {
    private val _chatIds = MutableStateFlow(emptyList<String>())
    val chatIds = _chatIds.asStateFlow()
    private val _messages = MutableStateFlow(emptyList<Message>())
    val messages = _messages.asStateFlow()

    init {
        _messages.value = listOf<Message>(
            Message("Hello", Date(), true),
            Message("Hi", Date(), false)
        )
    }

}