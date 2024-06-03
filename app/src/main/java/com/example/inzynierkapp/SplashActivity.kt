package com.example.inzynierkapp

import com.example.inzynierkapp.ui.theme.InzynierkappTheme
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            InzynierkappTheme {
                SplashScreen()

            }
        }
    }

    @Composable
    private fun SplashScreen() {
        var scale by remember { mutableStateOf(1f) }
        var opacity by remember { mutableStateOf(0f) }

        LaunchedEffect(key1 = true) {
            delay(1000) // opóźnienie przed rozpoczęciem animacji
            scale = 1.1f // minimalna zmiana skali
            opacity = 1f // zmiana przezroczystości
        }

        val animatedScale by animateFloatAsState(
            targetValue = scale,
            animationSpec = tween(
                durationMillis = 1000, // czas trwania animacji
                easing = CubicBezierEasing(0.15f,0.67f,0.5f,1.5f) // typ animacji
            )
        )

        val animatedOpacity by animateFloatAsState(
            targetValue = opacity,
            animationSpec = tween(
                durationMillis = 3000, // czas trwania animacji
                easing = CubicBezierEasing(0.15f,0.67f,0.5f,1.5f) // typ animacji
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf( Color(0xFFA9EEAC),Color(0xFF4A974D)), // gradient promieniowy
                        //center = Offset(0.5f, 0.3f), // punkt środkowy gradientu przesunięty w górę
                        radius = 500f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Inzynierkapp", // tekst przed ikonką
                    color = Color.White,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animatedOpacity) // dodajemy animację przezroczystości do tekstu
                )

                Image(
                    painter = painterResource(id = R.drawable.ikonka),
                    contentDescription = "Logo",
                    modifier = Modifier.scale(animatedScale)
                        .size(200.dp)
                        .padding(top = 50.dp) // dodajemy padding do obrazka
                        .alpha(animatedOpacity) // dodajemy animację przezroczystości do obrazka
                )
            }
        }

        LaunchedEffect(key1 = animatedScale) {
            if (animatedScale == 1.1f) {
                delay(2000) // opóźnienie równe czasowi trwania animacji
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }}}}
//    @Composable
//    private fun SplashScreen() {
//        var scale by remember { mutableStateOf(0f) }
//        var textScale by remember { mutableStateOf(0f) }
//
//        LaunchedEffect(key1 = true) {
//            delay(1000) // opóźnienie przed rozpoczęciem animacji
//            scale = 1f // zmiana skali powoduje animację
//            textScale = 1.5f // zmiana skali tekstu
//        }
//
//        val animatedScale by animateFloatAsState(
//            targetValue = scale,
//            animationSpec = tween(
//                durationMillis = 1000, // czas trwania animacji
//                easing = CubicBezierEasing(0.15f,0.67f,0.5f,1.5f) // typ animacji
//            )
//        )
//
//        val animatedTextScale by animateFloatAsState(
//            targetValue = textScale,
//            animationSpec = tween(
//                durationMillis = 1000, // czas trwania animacji
//                easing = CubicBezierEasing(0.15f,0.67f,0.5f,1.5f) // typ animacji
//            )
//        )
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(Color(0xC985E989), Color(0xFF346A22)), // gradient od jasnego do ciemnego
//                        startY = 0f,
//                        endY = Float.POSITIVE_INFINITY
//                    )
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Text(
//                    text = "Szlaki górskie", // tekst przed ikonką
//                    color = Color.White,
//                    fontSize = 30.sp,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .scale(animatedTextScale) // dodajemy animację skali do tekstu
//                )
//
//                Image(
//                    painter = painterResource(id = R.drawable.img),
//                    contentDescription = "Logo",
//                    modifier = Modifier.scale(animatedScale)
//                        .size(200.dp)
//                        .padding(top = 50.dp) // dodajemy padding do obrazka
//                )
//            }
//        }
//
//        LaunchedEffect(key1 = animatedScale) {
//            if (animatedScale == 1f) {
//                delay(2000) // opóźnienie równe czasowi trwania animacji
//                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//            }
//        }
//