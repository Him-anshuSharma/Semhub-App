package com.himanshu.semhub.ui.screens.homescreen.fragments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.himanshu.semhub.data.model.message.Message
import com.himanshu.semhub.ui.screens.homescreen.components.MessageCard
import com.himanshu.semhub.ui.viewmodel.messages.ChatViewModel

@Composable
fun ChatFragment(
    viewModel : ChatViewModel = hiltViewModel()
){
    Box(modifier = Modifier.fillMaxSize().padding(30.dp)){
        Column {
            LazyColumn {
                items(viewModel.messages.value) {
                    item: Message ->
                    MessageCard(
                        message = item.content,
                        left = !item.isUser
                    )
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