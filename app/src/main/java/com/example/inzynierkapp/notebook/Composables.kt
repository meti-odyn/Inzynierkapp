package com.example.inzynierkapp.notebook

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.inzynierkapp.note.NoteDao
import com.example.inzynierkapp.note.NoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    note: NoteModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var output by rememberSaveable { mutableStateOf<String>("") }
    val context = LocalContext.current
    val applicationCoroutineScope = rememberCoroutineScope()
    var enableWaitingScreen by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(note.content) {
        if (note.content.orEmpty().length >= 150) {
            output = "Generating summary..."
            applicationCoroutineScope.launch {
                val summary = withContext(Dispatchers.IO) {
//                    if (!Python.isStarted()) Python.start(AndroidPlatform(context))
//                    val py = Python.getInstance()
//                    val module = py.getModule("skrypt")
//                    return@withContext module.callAttr("generate_summary", note.content!!).toString()
                    return@withContext "generate_summary"
                }
                output = summary
                enableWaitingScreen = false
            }
        } else {
            output = "Data is too short to generate a summary. It must have at least 150 characters!"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Summary of ${note.name}") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (enableWaitingScreen) {
                WaitingScreen(modifier.fillMaxSize(), output)
            } else {
                SelectionContainer {
                    Text(
                        output,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultView(
    notesProvider: NoteDao,
    userEmail: String,
    onclick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notes = remember { mutableStateOf(listOf<NoteModel>()) }

    LaunchedEffect(userEmail) {
        notesProvider.getNotesByEmail(userEmail).collect { notesList ->
            if (notesList.isEmpty()) {
                scope.launch {
                    insertDefaultNoteIfEmpty(context, userEmail, notesProvider)
                    notesProvider.getNotesByEmail(userEmail).collect { notes.value = it }
                }
            } else {
                notes.value = notesList
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Notes") },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                val newNoteId = insertEmptyNoteAndGetId(context, userEmail, notesProvider)
                                notesProvider.getNotesByEmail(userEmail).collect { notes.value = it }
                                onclick(newNoteId)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create new note")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.padding(8.dp)
        ) {
            items(notes.value.size) { index ->
                NotePreview(notes.value[index], { onclick(notes.value[index].id) })
            }
        }
    }
}

suspend fun insertDefaultNoteIfEmpty(context: Context, userEmail: String, noteDao: NoteDao) {
    withContext(Dispatchers.IO) {
        val newNote = NoteModel(
            name = "Welcome Note",
            content = "This is your first note. Feel free to edit or delete it.",
            date = Date(),
            userEmail = userEmail
        )
        noteDao.addNote(newNote)
    }
}

suspend fun insertEmptyNoteAndGetId(context: Context, userEmail: String, noteDao: NoteDao): Int {
    return withContext(Dispatchers.IO) {
        val newNote = NoteModel(
            name = "New Note",
            content = "",
            date = Date(),
            userEmail = userEmail
        )
        noteDao.addNote(newNote)
        noteDao.getNewNoteID(userEmail) ?: 0
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotePreview(
    note: NoteModel,
    onclick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier
            .clickable { onclick() }
            .fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                note.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                note.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteContent(
    note: NoteModel,
    updateNote: (NoteModel) -> Unit,
    deleteNote: (NoteModel) -> Unit,
    navigateToSummary: () -> Unit,
    userEmail: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val REQUEST_CODE_CAMERA = 1
    val context = LocalContext.current

    val onCameraClick = {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_CAMERA
            )
        }
    }

    var title by remember { mutableStateOf(note.name) }
    var text by remember { mutableStateOf(note.content) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title ?: "Edit Note") },
                actions = {
                    IconButton(onClick = { navigateToSummary() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Summary")
                    }
                    IconButton(onClick = {
                        deleteNote(NoteModel(note.id, title, text, note.date, userEmail))
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCameraClick) {
                Icon(Icons.Default.Camera, contentDescription = "Camera")
            }
        }
    ) { paddingValues ->
        Column(
            modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title ?: "",
                onValueChange = { newValue ->
                    title = newValue
                    updateNote(NoteModel(note.id, title, text, note.date, userEmail))
                },
                label = { Text("Title") },
                textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            text?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = { newValue ->
                        text = newValue
                        updateNote(NoteModel(note.id, title, text, note.date, userEmail))
                    },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(fontSize = 16.sp),
                    singleLine = false,
                    maxLines = Int.MAX_VALUE
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { saveInCalendar(note, context) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save in Calendar")
            }
        }
    }
}

fun saveInCalendar(note: NoteModel, context: Context) {
    GlobalScope.launch {
        val datesToAdd = listOf(
            Date(), // Today's date
            Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000), // One week later
            Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000), // Two weeks later
        )
        for (date in datesToAdd) {
            val eventTime = date.time

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, note.name)
                putExtra(CalendarContract.Events.DESCRIPTION, note.content)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventTime)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventTime + 60 * 60 * 1000)
            }
            context.startActivity(intent)
            delay(10000)
        }
    }
}

@Composable
fun WaitingScreen(modifier: Modifier = Modifier, message: String = "Loading...") {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

class InitDb : Application() {
    override fun onCreate() {
        super.onCreate()
        appDatabase = AppDatabase.getDatabase(this)
    }

    companion object {
        var appDatabase: AppDatabase? = null
    }
}

@Composable
fun NoConnectionScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "No internet connection",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}
