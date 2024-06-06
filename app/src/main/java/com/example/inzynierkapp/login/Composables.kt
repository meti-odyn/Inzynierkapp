package com.example.inzynierkapp.login

import android.content.Context
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.inzynierkapp.ui.theme.InzynierkappTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import kotlin.concurrent.thread
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.inzynierkapp.MainActivity
import com.example.inzynierkapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


@Composable
fun AppContent(auth: FirebaseAuth, onSignedIn: () -> Unit) {
    var showSplashScreen by remember { mutableStateOf(true) }

    LaunchedEffect(showSplashScreen) {
        delay(2000)
        showSplashScreen = false
    }
    AuthOrMainScreen(auth, onSignedIn)


//        Crossfade(targetState = showSplashScreen, label = "") { isSplashScreenVisible ->
//            if (isSplashScreenVisible) {
//                SplashScreen {
//                    showSplashScreen = false
//                }
//            } else {
//                AuthOrMainScreen(auth)
//            }
//        }
}
//}


@Composable
fun AuthOrMainScreen(auth: FirebaseAuth, onSignedIn: () -> Unit) {
    //var user by rememberSaveable { mutableStateOf(auth.currentUser) }
    if (auth.currentUser == null) {
        AuthScreen(auth, onSignedIn)
    } else {
        MainScreen(auth.currentUser!!, { auth.signOut() } , onSignedIn)
    }
}

@Composable
fun AuthScreen(auth: FirebaseAuth,onSignedIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSignIn by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var myErrorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val activity = context as MainActivity

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.25f))
                .padding(25.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                if (!isSignIn) {
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("First Name") },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Last Name") },
                    )
                }
                else {
                    Image(
                        painter = painterResource(id = R.drawable.ikonka),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)

                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email
                    ),
                    visualTransformation = VisualTransformation.None
                )

                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password
                    ),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            val icon = if (isPasswordVisible) Icons.Default.Lock else Icons.Default.Search
                            Icon(imageVector = icon, contentDescription = "Toggle Password Visibility")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (myErrorMessage != null) {
                    Text(
                        text = myErrorMessage!!,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isSignIn) {
                            signIn(Firebase.auth, email, password, onSignedIn) { errorMessage -> myErrorMessage = errorMessage }
                        }
                        else {
                            signUp(Firebase.auth, email, password, firstName, lastName, onSignedIn)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(8.dp),
                ) {
                    Text(text = if (isSignIn) "Sign In" else "Sign Up", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val signInIntent = activity.googleSignInClient.signInIntent
                        activity.googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Sign-In",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sign in with Google", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(8.dp),
                ) {
                    ClickableText(
                        text = AnnotatedString(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Blue)) {
                                append(if (isSignIn) "Don't have an account? Sign Up" else "Already have an account? Sign In")
                            }
                        }.toString()),
                        onClick = {
                            myErrorMessage = null
                            email = ""
                            password = ""
                            isSignIn = !isSignIn
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(user: FirebaseUser, onSignOut: () -> Unit, onSignIn: () -> Unit) {
    var userProfile by remember { mutableStateOf<User?>(null) }

    // Fetch user profile from Firestore
    LaunchedEffect(user.uid) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(user.uid)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firstName = document.getString("firstName")
                    val lastName = document.getString("lastName")

                    userProfile = User(firstName, lastName, user.email ?: "")
                } else {
                    // Handle the case where the document doesn't exist
                }
            }
            .addOnFailureListener { e ->
                // Handle failure

            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        userProfile?.let {
            Text("Welcome, ${it.firstName} ${it.lastName}!")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onSignIn, modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Go to my Notes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onSignOut, modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Sign Out")
        }

    }
}