package com.example.inzynierkapp.animation

import com.example.inzynierkapp.ui.theme.InzynierkappTheme
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inzynierkapp.MainActivity
import com.example.inzynierkapp.R
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.blur

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter


@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            InzynierkappTheme {
                //SplashScreen()
                BackgroundScreen()

            }
        }
    }

    @Composable
    fun BackgroundScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.tlo),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Foreground content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome to Study Buddy",
                    color = Color.Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Harness the power of AI in learning.",
                    color = Color.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(180.dp))
                TransparentBlurredBox()

            }
        }
    }
    @Composable
    fun TransparentBlurredBox() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            // Background Box with Blur
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .blur(20.dp)
            )

            // Foreground content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "Make Learning Smarter",
                    fontSize = 22.sp,
                    color = Color.Black,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "From summaries to key questions, \nAI does the work for you",
                    fontSize = 15.sp,
                    color = Color.Black.copy(alpha = 1f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { startActivity(Intent(this@SplashActivity, MainActivity::class.java)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "Get started now",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }


    @Preview(showBackground = true) // Opcjonalnie: showBackground dodaje tło dla podglądu
    @Composable
    fun PreviewMyScreen() {
        BackgroundScreen() // Wywołaj swoją funkcję kompozycji tutaj
    }


    @Composable
    private fun SplashScreen() {
        var scale by remember { mutableFloatStateOf(1f) }
        var opacity by remember { mutableFloatStateOf(0f) }

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
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
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

//
//@Composable
//fun AuthScreen(auth: FirebaseAuth,onSignedIn: () -> Unit) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var firstName by remember { mutableStateOf("") }
//    var lastName by remember { mutableStateOf("") }
//    var isLoading by remember { mutableStateOf(false) }
//    var isSignIn by remember { mutableStateOf(true) }
//    var isPasswordVisible by remember { mutableStateOf(false) }
//    var myErrorMessage by remember { mutableStateOf<String?>(null) }
//    val context = LocalContext.current
//    val activity = context as MainActivity
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Card(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.25f))
//                .padding(25.dp)
//                .clip(RoundedCornerShape(16.dp)),
//            elevation = CardDefaults.cardElevation()
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//
//                if (!isSignIn) {
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    TextField(
//                        value = firstName,
//                        onValueChange = { firstName = it },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp),
//                        label = { Text("First Name") },
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    TextField(
//                        value = lastName,
//                        onValueChange = { lastName = it },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp),
//                        label = { Text("Last Name") },
//                    )
//                }
////                else {
////                    Image(
////                        painter = painterResource(id = R.drawable.ikonka),
////                        contentDescription = null,
////                        modifier = Modifier
////                            .size(150.dp)
////                            .clip(CircleShape)
////
////                    )
////                }
//                Spacer(modifier = Modifier.height(16.dp))
//                TextField(
//                    value = email,
//                    onValueChange = { email = it },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    label = { Text("Email") },
//                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
//                    keyboardOptions = KeyboardOptions.Default.copy(
//                        keyboardType = KeyboardType.Email
//                    ),
//                    visualTransformation = VisualTransformation.None
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//                TextField(
//                    value = password,
//                    onValueChange = { password = it },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    label = { Text("Password") },
//                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
//                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                    keyboardOptions = KeyboardOptions.Default.copy(
//                        keyboardType = KeyboardType.Password
//                    ),
//                    trailingIcon = {
//                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
//                            val icon = if (isPasswordVisible) Icons.Default.Lock else Icons.Default.RemoveRedEye
//                            Icon(imageVector = icon, contentDescription = "Toggle Password Visibility")
//                        }
//                    }
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                if (myErrorMessage != null) {
//                    Text(
//                        text = myErrorMessage!!,
//                        color = Color.Red,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp)
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Button(
//                    onClick = {
//                        if (isSignIn) {
//                            signIn(Firebase.auth, email, password, onSignedIn) { errorMessage -> myErrorMessage = errorMessage }
//                        }
//                        else {
//                            signUp(Firebase.auth, email, password, firstName, lastName, onSignedIn){ errorMessage -> myErrorMessage = errorMessage }
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(60.dp)
//                        .padding(8.dp),
//                ) {
//                    Text(text = if (isSignIn) "Sign In" else "Sign Up", fontSize = 18.sp)
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Button(
//                    onClick = {
//                        val signInIntent = activity.googleSignInClient.signInIntent
//                        activity.googleSignInLauncher.launch(signInIntent)
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(60.dp)
//                        .padding(8.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_google),
//                        contentDescription = "Google Sign-In",
//                        modifier = Modifier.size(24.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(text = "Sign in with Google", color = Color.Black)
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(50.dp)
//                        .padding(8.dp),
//                ) {
//                    ClickableText(
//                        text = AnnotatedString(buildAnnotatedString {
//                            withStyle(style = SpanStyle(color = Color.Blue)) {
//                                append(if (isSignIn) "Don't have an account? Sign Up" else "Already have an account? Sign In")
//                            }
//                        }.toString()),
//                        onClick = {
//                            myErrorMessage = null
//                            email = ""
//                            password = ""
//                            isSignIn = !isSignIn
//                        },
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//            }
//        }
//    }
//}