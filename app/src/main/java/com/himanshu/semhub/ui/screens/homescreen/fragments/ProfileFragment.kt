package com.himanshu.semhub.ui.screens.homescreen.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.himanshu.semhub.viewmodel.AuthViewModel

@Composable
fun Profile(
    viewModel: AuthViewModel = hiltViewModel(),
){

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.weight(0.7f))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(viewModel.getProfile()?.photoUrl).build(),
            contentDescription = "Profile image",
            contentScale = ContentScale.Fit,
            modifier = Modifier.weight(.7f)
        )
        Spacer(Modifier.weight(0.7f))
        Text(text = "Name: " + viewModel.getProfile()?.displayName)
        Spacer(modifier = Modifier.weight(0.1f))
        Text(text = "Email: " + viewModel.getProfile()?.email)
        Spacer(modifier = Modifier.weight(0.7f))
    }
}

@Composable
@Preview(showBackground = true)
fun ProfilePreview(){
    Profile(hiltViewModel())
}