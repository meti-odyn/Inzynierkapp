package com.example.inzynierkapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaquo.python.PyObject
import com.example.inzynierkapp.login.AppContent
import com.example.inzynierkapp.notebook.DefaultView
import com.example.inzynierkapp.notebook.NoteContent
import com.example.inzynierkapp.notebook.SummaryScreen
import com.example.inzynierkapp.ui.theme.InzynierkappTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.inzynierkapp.note.*
import com.example.inzynierkapp.notebook.AppDatabase
import com.example.inzynierkapp.notebook.WaitingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withContext
class MainActivity : ComponentActivity() {
    private lateinit var module: PyObject
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var navController: NavHostController

    private lateinit var noteDao: NoteDao
    private var userEmail: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        this.noteDao = db.noteDao

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
                    navController = rememberNavController()
                    var selectedNoteId by rememberSaveable { mutableIntStateOf(0) }

                    NavHost(navController, startDestination = "login") {

                        composable("login") {
                            AppContent(auth) { navController.navigate("notebook")
                                userEmail = auth.currentUser?.email}
                        }

                        composable("notebook") {
                            DefaultView(noteDao, userEmail ?: "", { id ->
                                selectedNoteId = id
                                navController.navigate("note")
                            })
                        }

                        composable("summary") {
                            var note by remember { mutableStateOf<NoteModel?>(null) }
                            LaunchedEffect(selectedNoteId) {
                                note = getNoteByIdAndEmail(selectedNoteId, userEmail!!)
                            }
                            if (note != null) {
                                SummaryScreen(note!!, { navController.popBackStack() })
                            } else {
                                WaitingScreen(Modifier.fillMaxSize())
                            }

                        }

                        composable("note") {
                            var note by remember { mutableStateOf<NoteModel?>(null) }
                            LaunchedEffect(selectedNoteId) {
                                userEmail?.let {
                                    note = getNoteByIdAndEmail(selectedNoteId, it)
                                }
                            }

                            note?.let {
                                NoteContent(
                                    note = it,
                                    updateNote = { updatedNote -> updateNote(updatedNote) },
                                    deleteNote = { noteToDelete -> deleteNote(noteToDelete) },
                                    navigateToSummary = { navController.navigate("summary") },
                                    userEmail = userEmail ?: "",
                                    navController = navController,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: run {
                                // Show loading or error state if note is null
                                Text("Loading...", Modifier.fillMaxSize(), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    userEmail = auth.currentUser?.email
                    navController.navigate("notebook")
                } else {
                    // Handle login failure
                }
            }
    }



    private fun initDB() {
        val db = AppDatabase.getDatabase(this)
        val testNote: NoteModel = NoteModel(1, "test", "test", Date())
        val noteDao = db.noteDao.insert(testNote)
        //val summaryDao = db.summaryDao
        //val questionDao = db.questionDao
    }
    private suspend fun getNoteByIdAndEmail(id: Int, email: String): NoteModel? {
        return withContext(Dispatchers.IO) {
            noteDao.getNoteByIdAndEmail(id, email)
        }
    }

    private fun updateNote (note: NoteModel) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            withContext(Dispatchers.IO) {
                noteDao.update(note)
            }
        }
    }

    private fun deleteNote (note: NoteModel) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            withContext(Dispatchers.IO) {
                noteDao.delete(note)
            }
        }
    }

//    private suspend fun getAllNotes(): List<NoteModel> {
//        return withContext(Dispatchers.IO) {
//            val notes = noteDao.getAllNotes().first()
//            Log.d("MainActivity", "Retrieved notes: $notes")
//            notes
//        }
//    }

}