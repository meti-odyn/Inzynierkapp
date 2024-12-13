package com.example.inzynierkapp.notebook

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.provider.MediaStore
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.example.inzynierkapp.backend.sendRequestToServer
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.core.spans.LinkSpan
import org.commonmark.parser.Parser


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SummaryScreen(
//    note: NoteModel,
//    onBack: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var output by rememberSaveable { mutableStateOf<String>("") }
//    val context = LocalContext.current
//    val applicationCoroutineScope = rememberCoroutineScope()
//    var enableWaitingScreen by rememberSaveable { mutableStateOf(true) }
//
//    LaunchedEffect(note.content) {
//        if (note.content.orEmpty().length >= 150) {
//            output = "Generating summary..."
//            applicationCoroutineScope.launch {
//                val summary = withContext(Dispatchers.IO) {
////                    if (!Python.isStarted()) Python.start(AndroidPlatform(context))
////                    val py = Python.getInstance()
////                    val module = py.getModule("skrypt")
////                    return@withContext module.callAttr("generate_summary", note.content!!).toString()
//                    return@withContext "generate_summary"
//                }
//                output = summary
//                enableWaitingScreen = false
//            }
//        } else {
//            output = "Data is too short to generate a summary. It must have at least 150 characters!"
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Summary of ${note.name}") },
//                navigationIcon = {
//                    IconButton(onClick = { onBack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Box(modifier = Modifier.padding(paddingValues)) {
//            if (enableWaitingScreen) {
//                WaitingScreen(modifier.fillMaxSize(), output)
//            } else {
//                SelectionContainer {
//                    Text(
//                        output,
//                        style = MaterialTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//            }
//        }
//    }
//}
@Composable
fun SummaryScreen(
    note: NoteModel,
    summary: String,         // Dodaj parametr `summary` typu String
    questions: String,       // Dodaj parametr `questions` typu String
    onBack: () -> Unit,      // Funkcja zwrotna dla przycisku "Back"
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier.padding(16.dp)) {
        item {
            Column {
                Text(
                    text = "Summary of ${note.name ?: "Brak nazwy"}",
                    modifier = Modifier.padding(0.dp, 12.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Summary:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = summary ?: "Brak streszczenia",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "Questions:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = questions ?: "Brak pytań",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
                Button(onClick = { onBack() }) {
                    Text("Go Back")
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

//@Composable
//fun MarkdownPreview(content: String) {
//    val context = LocalContext.current
//
//    AndroidView(
//        factory = { ctx ->
//            TextView(ctx).apply {
//                // Konfigurujemy Markwon z własnym parserem składni
//                val markwon = Markwon.builder(ctx)
//                    .usePlugin(object : AbstractMarkwonPlugin() {
//                        override fun configureParser(builder: Parser.Builder) {
//                            // Nie musimy zmieniać parsera Markdown – działa na poziomie tekstu
//                        }
//
//                        override fun configureVisitor(builder: MarkwonVisitor.Builder) {
//                            builder.on(Text::class.java) { visitor, textNode ->
//                                val text = textNode.literal
//                                val regex = Regex("\\{\\{(.*?)\\|(#[0-9a-fA-F]{6})\\}\\}")
//                                val spannable = SpannableStringBuilder()
//
//                                var lastIndex = 0
//                                regex.findAll(text).forEach { matchResult ->
//                                    val fullMatch = matchResult.value
//                                    val displayText = matchResult.groups[1]?.value ?: ""
//                                    val colorHex = matchResult.groups[2]?.value ?: "#000000"
//
//                                    // Dodajemy tekst przed dopasowaniem
//                                    spannable.append(text.substring(lastIndex, matchResult.range.first))
//
//                                    // Tworzymy kolorowy fragment
//                                    val coloredSpan = SpannableString(displayText)
//                                    try {
//                                        val color = android.graphics.Color.parseColor(colorHex)
//                                        coloredSpan.setSpan(
//                                            ForegroundColorSpan(color),
//                                            0,
//                                            displayText.length,
//                                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                                        )
//                                    } catch (e: IllegalArgumentException) {
//                                        // Ignorujemy błędy w kolorach
//                                    }
//                                    spannable.append(coloredSpan)
//
//                                    // Ustawiamy nowy indeks
//                                    lastIndex = matchResult.range.last + 1
//                                }
//
//                                // Dodajemy pozostały tekst
//                                spannable.append(text.substring(lastIndex))
//
//                                visitor.builder().append(spannable)
//                            }
//                        }
//                    })
//                    .build()
//
//                // Renderowanie Markdown
//                markwon.setMarkdown(this, content)
//            }
//        },
//        update = { view ->
//            val markwon = Markwon.builder(context).build()
//            markwon.setMarkdown(view, content)
//        }
//    )
//}


@Composable
fun MarkdownPreview(content: String) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                Markwon.create(ctx).setMarkdown(this, content)
            }
        },
        update = { view ->
            Markwon.create(context).setMarkdown(view, content)
        }
    )
}

//@Composable
//fun NotePreview(
//    note: NoteModel,
//    onclick: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//
//    Card(modifier.clickable { onclick() }) {
//        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//           Text(note.name ?: "" , fontSize = 18.sp, fontWeight = FontWeight.Bold)
//            //Text(note.content?.substring(0, minOf(headLength, note.content.length)) ?: "")
//           // MarkdownPreview(note.content?.take(100000000) ?: "") // Ogranicz podgląd do 100 znaków
//
//        }
//    }
//}
//@Composable
//fun NoteContent(
//    note: NoteModel,
//    updateNote: (NoteModel) -> Unit,
//    deleteNote: (NoteModel) -> Unit,
//    navigateToSummary: () -> Unit,
//    userEmail: String,
//    navController: NavHostController,
//    modifier: Modifier = Modifier
//) {
//    val REQUEST_CODE_CAMERA = 1
//    val context = LocalContext.current
//
//    val onCameraClick = {
//        if (ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            if (intent.resolveActivity(context.packageManager) != null) {
//                context.startActivity(intent)
//            } else {
//                Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            ActivityCompat.requestPermissions(
//                context as Activity,
//                arrayOf(Manifest.permission.CAMERA),
//                REQUEST_CODE_CAMERA
//            )
//        }
//    }
//
//
//
//    var title by remember { mutableStateOf(note.name) }
//    var text by remember { mutableStateOf(note.content) }
//
//    LazyColumn(
//        modifier
//            .fillMaxSize()
//            .padding(4.dp)
//    ) {
//        item {
//            TextField(
//                value = title ?: "",
//                onValueChange = { newValue ->
//                    title = newValue
//                    updateNote(NoteModel(note.id, title, text, note.date, userEmail))
//                },
//                modifier = Modifier.fillMaxWidth(),
//                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
//            )
//        }
//
//        item {
//            text?.let {
//                TextField(
//                    value = it,
//                    onValueChange = { newValue ->
//                        text = newValue
//                        updateNote(NoteModel(note.id, title, text, note.date, userEmail))
//                    },
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
//
//        item {
//            Button(onClick = {
//                deleteNote(NoteModel(note.id, title, text, note.date, userEmail))
//                navController.popBackStack()
//
//            }) {
//                Text("Delete Note")
//            }
//        }
//
//        item {
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Button(onClick = { saveInCalendar(note, context) }) {
//                    Text("Save in Calendar")
//                }
//                Button(onClick = { navigateToSummary() }) {
//                    Text("Summary")
//                }
//            }
//        }
//
//        item {
//            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
//                FloatingActionButton(
//                    onClick = onCameraClick,
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Camera,
//                        contentDescription = "Camera Button"
//                    )
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotePreview(
    note: NoteModel,
    onclick: () -> Unit,
    modifier: Modifier = Modifier
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
        // Obsługa kamery
    }

    var title by remember { mutableStateOf(note.name) }
    var text by remember { mutableStateOf(note.content) }
    var showMarkdownPreview by remember { mutableStateOf(false) }

    if (showMarkdownPreview) {
            MarkdownPreview(text)
        } 
        else{
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
                    title = if (newValue.isBlank()) "Untitled" else newValue
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
        Button(onClick = { showMarkdownPreview = !showMarkdownPreview }) {
                    Text(if (showMarkdownPreview) "Edit" else "Preview Markdown")
                }}
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
