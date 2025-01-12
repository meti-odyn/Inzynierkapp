package com.example.inzynierkapp.note

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
//import com.example.inzynierkapp.notebook.NoteRecord
import java.time.LocalDateTime
import java.util.Date

@Entity(tableName = "notes")
data class NoteModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String? = "",
    val content: String?,
    val date: Date = Date(),
    val userEmail: String? = null,
)

@Entity(tableName = "summaries")
data class Summary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val noteId: Int,
    val content: String?,
    val date: Date = Date(),
)
