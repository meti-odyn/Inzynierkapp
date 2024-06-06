package com.example.inzynierkapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.inzynierkapp.notebook.DefaultView
import com.example.inzynierkapp.notebook.SummaryScreen
import com.example.inzynierkapp.notebook.Note
import com.example.inzynierkapp.notebook.NoteRecord
import com.example.inzynierkapp.notebook.NoteContent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    private lateinit var module: PyObject
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            InzynierkappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var selectedNoteId by rememberSaveable { mutableIntStateOf(0) }

                    NavHost(navController, "login") {

                        composable("login") {
                            AppContent(auth) { navController.navigate("notebook") }
                        }

                        composable("notebook") {
                            DefaultView( getAllNotes(), { id -> selectedNoteId = id.also { navController.navigate("note") } })
                        }
                        composable("summary/{noteId}") { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId")?.toInt()
                            val note = getNote(noteId!!)
                            SummaryScreen(note = note, onBack = { navController.popBackStack() })
                        }
                        composable("note") {
                            NoteContent(
                                getNote(selectedNoteId),
                                updateNote = {/* */},
                                navigateToSummary = { navController.navigate("summary/${selectedNoteId}")},
                                Modifier.fillMaxSize() )
                        }

                     }
                }
            }
        }
    }

    val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Handle error
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Handle successful login
                } else {
                    // Handle login failure
                }
            }
    }


    private fun getNote(id: Int): Note = Note(id, "title $id")

    private fun getAllNotes(): List<Note> = (0..10).map { Note(it, "title $it") }

}


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
fun SortAndDisplayResult(context: Context) {
    val text =
        "The term NLP can refer to two different things: Natural Language Processing (NLP): This is a field of computer science and artificial intelligence concerned with enabling computers to understand and manipulate human language. NLP techniques are used in a wide range of applications, including: " +
                "Machine translation: translating text from one language to another " +
                "Speech recognition: converting spoken words into text " +
                "Text summarization: creating a concise summary of a longer piece of text " +
                "Chatbots: computer programs that can simulate conversation with human users " +
                "Sentiment analysis: determining the emotional tone of a piece of text " +
                "Neuro-linguistic programming (NLP): This is a controversial approach to communication, personal development, and psychotherapy that is not generally accepted by the scientific community. NLP claims that there is a connection between neurological processes, language, and acquired behavioral patterns, and that these can be changed to achieve specific goals in life. However, there is no scientific evidence to support these claims, and NLP is often criticized for being pseudoscience." +
                "\nIt is important to be aware of the difference between these two meanings of NLP, as they are completely unrelated fields."
    var output by remember { mutableStateOf<String?>(null) }
    thread {
        if (!Python.isStarted()) Python.start(AndroidPlatform(context))
        val py = Python.getInstance()
        val module = py.getModule("skrypt")
        output = module.callAttr("generate_summary", text).toString()
    }
    Text(
        text = output ?: "Posortowane liczby: jeszce nie!",
        modifier = Modifier.fillMaxSize(),
    )
}

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




@Composable
fun AuthOrMainScreen(auth: FirebaseAuth, onSignedIn: () -> Unit) {
    //var user by rememberSaveable { mutableStateOf(auth.currentUser) }
    if (auth.currentUser == null) {
        AuthScreen(onSignedIn)
    } else {
        MainScreen(auth.currentUser!!, { auth.signOut() } , onSignedIn)
    }
}


@Composable
fun AuthScreen(onSignedIn: () -> Unit) {
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

// Function to handle sign-in errors
private fun onSignInError(errorMessage: String) {
    // Handle the sign-in error as needed
    // For now, we'll print the error message
    println("Sign-in error: $errorMessage")
}




@Composable
fun MainScreen(user: FirebaseUser, onSignOut: () -> Unit, onSignIn: () -> Unit) {
    var userProfile by rememberSaveable { mutableStateOf<User?>(null) }

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



private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignedIn: () -> Unit,
    onSignInError: (String) -> Unit // Callback for sign-in error
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                onSignedIn()
            } else {
                // Handle sign-in failure
                onSignInError("Invalid email or password")
            }
        }
}


private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    onSignedIn: () -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser

                // Create a user profile in Firestore
                val userProfile = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email
                )

                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users")
                    .document(user!!.uid)
                    .set(userProfile)
                    .addOnSuccessListener {
                        onSignedIn()
                    }
                    .addOnFailureListener {
                        //handle exception

                    }
            } else {
                // Handle sign-up failure

            }
        }
}


data class User(
    val firstName: String?,
    val lastName: String?,
    val email: String
)