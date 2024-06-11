package com.example.inzynierkapp.note

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: NoteModel)

    @Update
    suspend fun update(note: NoteModel)

    @Query("SELECT * FROM notes WHERE userEmail = :email")
    fun getNotesByEmail(email: String): Flow<List<NoteModel>>

    @Query("SELECT * FROM notes WHERE id = :id AND userEmail = :email")
    suspend fun getNoteByIdAndEmail(id: Int, email: String): NoteModel?

    @Query("SELECT id FROM notes WHERE userEmail = :email ORDER BY date DESC LIMIT 1")
    fun getNewNoteID(email: String): Int?

}