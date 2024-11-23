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
import com.google.firebase.firestore.FirebaseFirestore

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

        // Check if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userEmail = currentUser.email
            userEmail?.let { email ->
                fetchNotesFromFirebase(email) // Synchronizuj dane z Firebase
            }
        }

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
                            AppContent(auth) {
                                navController.navigate("notebook")
                                userEmail = auth.currentUser?.email
                                userEmail?.let { email ->
                                    fetchNotesFromFirebase(email) // Synchronizuj dane po zalogowaniu
                                }
                            }
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
        val noteDao = db.noteDao.addNote(testNote)
        //val summaryDao = db.summaryDao
        //val questionDao = db.questionDao
    }
    private fun addNote(note: NoteModel) {
        CoroutineScope(Dispatchers.IO).launch {
            noteDao.addNote(note) // Add to local database
            userEmail?.let {
                syncNotesToFirebase(it) // Sync with Firebase
            }
        }
    }

    private fun updateNote(note: NoteModel) {
        CoroutineScope(Dispatchers.IO).launch {
            noteDao.update(note) // Update locally
            userEmail?.let {
                syncNotesToFirebase(it) // Sync with Firebase
            }
        }
    }

    private fun deleteNote(note: NoteModel) {
        CoroutineScope(Dispatchers.IO).launch {
            noteDao.delete(note) // Delete locally
            userEmail?.let {
                syncNotesToFirebase(it) // Sync with Firebase
            }
        }
    }

    private fun logout() {
        userEmail?.let {
            syncNotesToFirebase(it) // Sync before logging out
        }
        auth.signOut()
        clearLocalDatabase() // Clear local database
        navController.navigate("login") // Navigate to login screen
    }

    private fun syncNotesToFirebase(userEmail: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val notes = noteDao.getNotesByEmail(userEmail).first() // Get local notes
            val notesMap = notes.map {
                mapOf(
                    "id" to it.id,
                    "name" to it.name,
                    "content" to it.content,
                    "date" to it.date.toString()
                )
            }
            val firestore = FirebaseFirestore.getInstance()
            val userDoc = firestore.collection("notes").document(userEmail)
            userDoc.set(mapOf("notes" to notesMap)) // Save to Firebase
        }
    }

    private fun fetchNotesFromFirebase(userEmail: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userDoc = firestore.collection("notes").document(userEmail)

        userDoc.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val notesList = document.get("notes") as? List<Map<String, Any>> ?: emptyList()
                val notes = notesList.map {
                    NoteModel(
                        id = (it["id"] as Long).toInt(),
                        name = it["name"] as? String,
                        content = it["content"] as? String,
                        date = (it["date"] as String).let { dateStr -> Date(dateStr) },
                        userEmail = userEmail
                    )
                }
                saveNotesToLocalDatabase(notes)
            }
        }.addOnFailureListener { exception ->
            Log.e("FirebaseSync", "Error fetching notes: ", exception)
        }
    }

    private fun saveNotesToLocalDatabase(notes: List<NoteModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            noteDao.clearAll() // Clear local database
            notes.forEach { noteDao.addNote(it) } // Save new data
        }
    }

    private suspend fun getNoteByIdAndEmail(id: Int, email: String): NoteModel? {
        return withContext(Dispatchers.IO) {
            noteDao.getNoteByIdAndEmail(id, email)
        }
    }

    private fun clearLocalDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            noteDao.clearAll()
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