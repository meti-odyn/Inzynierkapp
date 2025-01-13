package com.example.inzynierkapp.login

//import androidx.compose.foundation.layout.RowScopeInstance.weight

//import androidx.compose.foundation.layout.ColumnScopeInstance.weight
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.inzynierkapp.R
import com.example.inzynierkapp.ui.theme.GradientBackground
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(auth: FirebaseAuth, onSignedIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isSignIn by remember { mutableStateOf(true) } // Przełączanie logowanie/rejestracja
    var myErrorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Tło
        Image(
            painter = painterResource(id = R.drawable.tlo), // Ustaw swoje tło
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Nagłówek
            Text(
                text = if (isSignIn) "Log In" else "Sign Up",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Pole Email
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("Email") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedTextColor = Color.Black
                )
            )

            // Imię i nazwisko dla rejestracji
            if (!isSignIn) {
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = { Text("First Name") },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedTextColor = Color.Black
                    )
                )

                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = { Text("Last Name") },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedTextColor = Color.Black
                    )
                )
            }

            // Pole Password
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("Password") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        Icon(imageVector = icon, contentDescription = "Toggle Password Visibility")
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedTextColor = Color.Black
                )
            )

            // Pole Confirm Password
            if (!isSignIn) {
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = { Text("Confirm Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false, // Wyłączenie autouzupełniania
                        keyboardType = KeyboardType.Password
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedTextColor = Color.Black
                    )
                )
            }

            // Wyświetlanie błędów
            if (myErrorMessage != null) {
                Text(
                    text = myErrorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Przycisk logowania/rejestracji
            Button(
                onClick = {
                    if (isSignIn) {
                        // Logowanie
                        signIn(auth, email, password, onSignedIn) { errorMessage ->
                            myErrorMessage = errorMessage
                        }
                    } else {
                        // Rejestracja
                        if (password == confirmPassword) {
                            signUp(auth, email, password, firstName, lastName, onSignedIn) { errorMessage ->
                                myErrorMessage = errorMessage
                            }
                        } else {
                            myErrorMessage = "Passwords do not match"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = if (isSignIn) "Log In" else "Sign Up",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Przełącznik logowanie/rejestracja
            TextButton(
                onClick = {
                    isSignIn = !isSignIn
                    myErrorMessage = null // Reset błędu przy przełączaniu
                    email = ""
                    password = ""
                    confirmPassword = ""
                    firstName = ""
                    lastName = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isSignIn) "Don't have an account? Sign Up" else "Already have an account? Log In",
                    color = Color.Black
                )
            }
        }
    }
}


@Preview(showBackground = true) // Opcjonalnie: showBackground dodaje tło dla podglądu
@Composable
fun PreviewMyScreen() {
    // Przygotowanie mockowych funkcji i danych
    val auth: FirebaseAuth by lazy { Firebase.auth }
    val onSignedInMock = {}

    // Wywołanie AuthScreen z mockowanymi wartościami
    AuthScreen(auth = auth, onSignedIn = onSignedInMock)
}


//@Composable
//fun MainScreen(user: FirebaseUser, onSignOut: () -> Unit, onSignIn: () -> Unit) {
//    var userProfile by remember { mutableStateOf<User?>(null) }
//
//    // Fetch user profile from Firestore
//    LaunchedEffect(user.uid) {
//        val firestore = FirebaseFirestore.getInstance()
//        val userDocRef = firestore.collection("users").document(user.uid)
//
//        userDocRef.get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val firstName = document.getString("firstName")
//                    val lastName = document.getString("lastName")
//
//                    userProfile = User(firstName, lastName, user.email ?: "")
//                } else {
//                    // Handle the case where the document doesn't exist
//                }
//            }
//            .addOnFailureListener { e ->
//                // Handle failure
//
//            }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        userProfile?.let {
//            Text("Welcome, ${it.firstName} ${it.lastName}!")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onSignIn, modifier = Modifier.fillMaxWidth().height(50.dp)
//        ) {
//            Text("Go to my Notes")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onSignOut, modifier = Modifier.fillMaxWidth().height(50.dp)
//        ) {
//            Text("Sign Out")
//
//        }
//
//    }
//}

fun onSignOut(auth: FirebaseAuth, navController: NavController) {
    auth.signOut()  // Użycie przekazanej instancji FirebaseAuth
    navController.navigate("login") {
        popUpTo("main") { inclusive = true }  // Usunięcie stosu nawigacyjnego
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(user: FirebaseUser, onSignOut: (NavController) -> Unit, onSignedIn: () -> Unit, navController: NavHostController, onNavigateToSection: (String) -> Unit) {
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
                }
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        userProfile?.let {
                            Text(text = "Hi, ${it.firstName} 👋", style = MaterialTheme.typography.headlineSmall)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            onSignOut(navController)
                            //onSignedIn() // Przejście do ekranu logowania po wylogowaniu
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Log Out")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.Black
                    )
                )
            },

            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    // Sekcja z przyciskami "category"
                    Text(
                        text = "jaka nauka na dzis, wariacie",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
    //                ['#dcd7cb',
    //                    '#ddd8cc',
    //                    '#d8d7cc',
    //                    '#dad5c9',
    //                    '#dbd6ca',
    //                    '#d5d7cd',
    //                    '#d7d6cb',
    //                    '#d9d4c8',
    //                    '#ded7c9',
    //                    '#e0d9cc']

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CategoryCard(
                            title = "Notes",
                            color = Color(0xFFddd8cc),
                            onClick = { onNavigateToSection("Notebook") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CategoryCard(
                            title = "Summaries",
                            color = Color(0xFF84a5ac),
                            onClick = { onNavigateToSection("Summaries") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CategoryCard(
                            title = "Questions",
                            color = Color(0xFF9badae),
                            onClick = { onNavigateToSection("Questions") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CategoryCard(
                            title = "Tests",
                            color = Color(0xFFD3D3D3),
                            onClick = { onNavigateToSection("Tests") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    content = {
                        IconButton(onClick = { onNavigateToSection("Home") }) {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { onNavigateToSection("App Info") }) {
                            Icon(Icons.Default.Info, contentDescription = "App Info") // Dodano App Info
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { onNavigateToSection("Profile") }) {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                        }
                    }
                )
            }
        )
    }
}


@Composable
fun CategoryCard(title: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
    }
}





