package com.example.inzynierkapp.animation

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navigateToAuthOrMainScreen: () -> Unit) {
    // Rotate effect for the image
    var rotationState by remember { mutableFloatStateOf(0f) }

    // Navigate to AuthOrMainScreen after a delay
    LaunchedEffect(true) {
        // Simulate a delay of 2 seconds
        delay(2000)
        // Call the provided lambda to navigate to AuthOrMainScreen
        navigateToAuthOrMainScreen()
    }

    // Rotation effect animation
    LaunchedEffect(rotationState) {
        while (true) {
            delay(16) // Adjust the delay to control the rotation speed
            rotationState += 1f
        }
    }

    // Splash screen UI with transitions
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = TweenSpec(durationMillis = 500), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        //Image(
        //painter = painterResource(id = R.drawable.logo),
        //contentDescription = null,
        //modifier = Modifier
        //.size(150.dp)
        //.clip(CircleShape)
        //.scale(scale)
        //.rotate(rotationState) // Apply the rotation effect
        // )
    }
}