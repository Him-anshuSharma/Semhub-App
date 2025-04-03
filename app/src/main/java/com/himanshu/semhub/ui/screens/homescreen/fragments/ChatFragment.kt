package com.himanshu.semhub.ui.screens.homescreen.fragments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.himanshu.semhub.ui.screens.homescreen.components.MessageCard
import com.himanshu.semhub.ui.viewmodel.messages.ChatViewModel
import com.himanshu.semhub.ui.viewmodel.messages.MessageState

@Composable
fun ChatFragment(
    viewModel : ChatViewModel = hiltViewModel()
){

    var userMessage by remember { mutableStateOf("") }
    LaunchedEffect(viewModel.messages){

    }
    val messages = viewModel.messages.collectAsState()
    val messageState = viewModel.messageState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(30.dp)){
        Column {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.messages.value.size){index ->
                    val message = messages.value[index].content
                    val left = !messages.value[index].isUser
                    MessageCard(message = message, left = left)
                }
            }
            if(messageState.value is MessageState.Typing){
                MessageCard(message = "Typing...", left = true)
            }
            else if(messageState.value is MessageState.Error){
                val error = messageState.value as MessageState.Error
                MessageCard(message = error.message, left = true, color = Color.Red)
            }
            Row{
                TextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                )
                Button(onClick = {
                    viewModel.sendMessage(userMessage)
                    userMessage = ""
                }) {
                    //icon with send image
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    ChatFragment()
}