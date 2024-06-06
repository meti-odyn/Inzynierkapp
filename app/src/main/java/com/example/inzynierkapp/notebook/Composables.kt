package com.example.inzynierkapp.notebook

import android.Manifest
import android.app.Activity
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun SummaryScreen(note: Note, onBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Summary of ${note.title}", style = MaterialTheme.typography.titleLarge)
        Text(text = note.text, style = MaterialTheme.typography.displaySmall)
        Button(onClick = { onBack() }) {
            Text("Go Back")
        }
    }
}
@Composable
fun DefaultView(notes: List<Note>, onclick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier.padding(12.dp)) {
            Text("Your notes:", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))

            LazyVerticalGrid( GridCells.Adaptive(minSize = 180.dp), modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes.size) { index ->
                    NotePreview(notes[index], { onclick(notes[index].id) })
                }
            }
        }

        Surface(
            modifier = Modifier
                .size(100.dp) // Zwiększony rozmiar Surface
                .align(Alignment.BottomEnd)
                .padding(16.dp), // Dodane padding od dolnego końca
            shape = CircleShape,
            color = Color(0xFFA1A1E9) // Kolor granatowy
        ) {
            IconButton(onClick = { /* onclick(insertEmptyNote()) */ }, Modifier.padding(12.dp)) { // Zwiększony padding wewnątrz IconButton
                Icon(Icons.Default.Add, contentDescription = "Create new note", Modifier.size(30.dp)) // Zwiększony rozmiar ikony
            }
        }
    }
}
@Composable
fun NotePreview (note: Note, onclick: () -> Unit, modifier: Modifier = Modifier, headLength: Int = 50) {

    Card(modifier.clickable { onclick() }) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(note.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(note.text.substring(0, minOf( headLength, note.text.length)))
        }
    }
}

//@Composable
//fun NoteContent (note: Note, updateNote: (Note) -> Unit, navigateToSummary: () -> Unit, modifier: Modifier = Modifier) {
//    val REQUEST_CODE_CAMERA = 1
//    val context = LocalContext.current
////    val onCameraClick = {
////        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////        if (intent.resolveActivity(context.packageManager) != null) {
////            context.startActivity(intent)
////        } else {
////            Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
////        }
////    }
//
//    val onCameraClick = {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            if (intent.resolveActivity(context.packageManager) != null) {
//                context.startActivity(intent)
//            } else {
//                Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
//        }
//    }
//    var title by remember { mutableStateOf(note.title) }
//    var text by remember { mutableStateOf(note.text) }
//    LazyColumn(modifier.fillMaxSize().padding(4.dp)) {
//
//        item {
//            TextField(title, { newValue -> title = newValue. also { updateNote(Note(note.id, title, text)) } }, Modifier.fillMaxWidth(),
//                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold))
//        }
//
//        item {
//            TextField(text, { newValue -> text = newValue. also { updateNote(Note(note.id, title, text)) } }, Modifier.fillMaxSize())
//        }
//
//        item {
//            Button(onClick = { saveInCalendar(note, context) }) {
//                Text("Save in Calendar")
//            }
//    }
//        item{
//        FloatingActionButton(
//            onClick = onCameraClick,
//            modifier = Modifier
//                .padding(16.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Camera,
//                contentDescription = "Camera Button"
//            )
//        }
//
//            }
//        item{
//            Button(onClick = { navigateToSummary() }) {
//                Text("Summary")
//            }}
//
//}
//}
@Composable
fun NoteContent(note: Note, updateNote: (Note) -> Unit, navigateToSummary: () -> Unit, modifier: Modifier = Modifier) {
    val REQUEST_CODE_CAMERA = 1
    val context = LocalContext.current

    val onCameraClick = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        }
    }
    var title by remember { mutableStateOf(note.title) }
    var text by remember { mutableStateOf(note.text) }
    LazyColumn(modifier.fillMaxSize().padding(4.dp)) {

        item {
            TextField(title, { newValue -> title = newValue. also { updateNote(Note(note.id, title, text)) } }, Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold))
        }

        item {
            TextField(text, { newValue -> text = newValue. also { updateNote(Note(note.id, title, text)) } }, Modifier.fillMaxSize())
        }

        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
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
fun saveInCalendar(note: Note, context: Context) {
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
                putExtra(CalendarContract.Events.TITLE, note.title)
                putExtra(CalendarContract.Events.DESCRIPTION, note.text)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventTime)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventTime + 60 * 60 * 1000)
            }
            context.startActivity(intent)
            delay(10000) // wait for 10 seconds before launching the next intent
        }
    }
}
