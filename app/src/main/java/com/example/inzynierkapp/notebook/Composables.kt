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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.inzynierkapp.note.NoteDao
import com.example.inzynierkapp.note.NoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.concurrent.thread


@Composable
fun SummaryScreen(note: NoteModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier.padding(16.dp)) {
        item {
            Column {
                Text(text = "Summary of ${note.name}", style = MaterialTheme.typography.titleLarge)
                var output by rememberSaveable { mutableStateOf<String>("") }
                val context = LocalContext.current

                if (note.content.orEmpty().length >= 150) {
                    output = "generating..."
                    //LaunchedEffect(key1 = note)
                    thread {
                        if (!Python.isStarted()) Python.start(AndroidPlatform(context))
                        val py = Python.getInstance()
                        val module = py.getModule("skrypt")
                        output = module.callAttr("generate_summary", note.content!!).toString()
                    }
                } else output =
                    "data is too short to generate summary, it has to have at least 150 characters!"

                Text(output, style = MaterialTheme.typography.bodySmall)
                Button(onClick = { onBack() }) {
                    Text("Go Back")
                }
            }
        }
    }
}

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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier.padding(12.dp)) {
            Text(
                "Your notes:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            LazyVerticalGrid(
                GridCells.Adaptive(minSize = 140.dp), modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes.value.size) { index ->
                    NotePreview(notes.value[index], { onclick(notes.value[index].id) })
                }
            }
        }

        Surface(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            shape = CircleShape,
            color = Color(0xFFA1A1E9)
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        val newNoteId = insertEmptyNoteAndGetId(context, userEmail, notesProvider)
                        notesProvider.getNotesByEmail(userEmail).collect { notes.value = it }
                        onclick(newNoteId)
                    }
                },
                Modifier.padding(12.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Create new note",
                    Modifier.size(30.dp)
                )
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
        noteDao.insert(newNote)
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
        noteDao.insert(newNote)
        noteDao.getNewNoteID(userEmail) ?: 0
    }
}

@Composable
fun NotePreview(
    note: NoteModel,
    onclick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Card(modifier.clickable { onclick() }) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(note.name ?: "" , fontSize = 18.sp, fontWeight = FontWeight.Bold)
            //Text(note.content?.substring(0, minOf(headLength, note.content.length)) ?: "")
        }
    }
}
@Composable
fun NoteContent(
    note: NoteModel,
    updateNote: (NoteModel) -> Unit,
    navigateToSummary: () -> Unit,
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

    LazyColumn(
        modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        item {
            TextField(
                value = title ?: "",
                onValueChange = { newValue ->
                    title = newValue
                    updateNote(NoteModel(note.id, title, text, note.date))
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
        }

        item {
            text?.let {
                TextField(
                    value = it,
                    onValueChange = { newValue ->
                        text = newValue
                        updateNote(NoteModel(note.id, title, text, note.date))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { saveInCalendar(note, context) }) {
                    Text("Save in Calendar")
                }
                Button(onClick = { navigateToSummary() }) {
                    Text("Summary")
                }
            }
        }

        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                FloatingActionButton(
                    onClick = onCameraClick,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Camera Button"
                    )
                }
            }
        }
    }
}

fun saveInCalendar(note: NoteModel, context: Context) {
    GlobalScope.launch {
        val datesToAdd = listOf(
            Date(), // dzisiejsza data
            Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000), // data za tydzień
            Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000), // data za dwa tygodnie
            // dodaj więcej dat według potrzeb
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
            delay(10000) // wait for 10 seconds before launching the next intent
        }
    }
}
@Composable
fun WaitingScreen(modifier: Modifier =Modifier, message: String = "loading") = Card (modifier) { Text (message) }


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

    Text(text = "Brak połączenia z internetem")
}