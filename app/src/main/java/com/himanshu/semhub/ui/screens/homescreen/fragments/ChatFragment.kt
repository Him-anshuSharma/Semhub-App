package com.himanshu.semhub.ui.screens.homescreen.fragments

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.himanshu.semhub.ui.screens.homescreen.components.MessageCard
import com.himanshu.semhub.ui.viewmodel.chat.ChatViewModel
import com.himanshu.semhub.ui.viewmodel.chat.MessageState

@Composable
fun ChatFragment(
    viewModel : ChatViewModel = hiltViewModel()
){

    var userMessage by remember { mutableStateOf("") }

    val TAG = "ChatFragment"

    val messages = viewModel.messages.collectAsState()
    val messageState = viewModel.messageState.collectAsState()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val listState = rememberLazyListState()

    LaunchedEffect(messages.value.size) {
        Log.d("ChatFragment", "LaunchedEffect triggered, messages: $messages")
        if (messages.value.isNotEmpty()) {
            listState.animateScrollToItem(messages.value.lastIndex)
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveChat()
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(10.dp)){
        if(messageState.value != MessageState.Loading){
            Column {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = listState,
                    ) {
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
                    Toast.makeText(LocalContext.current, error.message, Toast.LENGTH_SHORT).show()
                }
                Row{
                    TextField(
                        value = userMessage,
                        modifier = Modifier.width(screenWidth*(0.75f)),
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
        else{
            Text("Loading Chat", fontSize = 32.sp)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    ChatFragment()
}