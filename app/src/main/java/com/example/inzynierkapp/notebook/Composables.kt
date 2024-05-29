package com.example.inzynierkapp.notebook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DefaultView(notes: List<Note>, onclick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.padding(12.dp)) {
        Button( { /* onclick(insertEmptyNote()) */ }, Modifier.width(116.dp).padding(8.dp))  {
            Text("Create new note")
        }

        LazyVerticalGrid( GridCells.Adaptive(minSize = 80.dp), modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes.size) { index ->
                NotePreview(notes[index], { onclick(notes[index].id) })
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

@Composable
fun NoteContent (note: Note, updateNote: (Note) -> Unit, modifier: Modifier = Modifier) {

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
    }
}