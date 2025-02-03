package com.example.inzynierkapp.note

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class NoteModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String? = "",
    val content: String?,
    val date: Date = Date(),
    val userEmail: String? = null,
    var summary: String? = null,
    var questions: String? = null
)
