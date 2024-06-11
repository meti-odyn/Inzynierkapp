package com.example.inzynierkapp.notebook

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.inzynierkapp.login.User
import com.example.inzynierkapp.note.NoteDao
import com.example.inzynierkapp.note.NoteModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject


@Entity(tableName = "questions", foreignKeys = [ForeignKey(
    entity = User::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("noteId"),
    onDelete = ForeignKey.CASCADE
)]
)
data class Questions(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val content: String,
    val ans: String,
    val data: Date,
)


@Entity(tableName = "summaries", foreignKeys = [ForeignKey(
    entity = NoteModel::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("noteId"),
    onDelete = ForeignKey.CASCADE
)]
)
data class Summary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val content: String,
    val data: Date,
)


//class NoteRepository(context: Context) {
//    private val noteDao = AppDatabase.getDatabase(context).noteDao()
//
//    suspend fun insert(note: NoteRecord): Long {
//        return noteDao.insert(note)
//    }
//
//}


@Dao
interface SummaryDao {
    @Insert
    suspend fun insert(summary: Summary)

    @Query("SELECT * FROM summaries")
    suspend fun getAllUsers(): List<User>
}
@Dao
interface QuestionDao {
    @Insert
    suspend fun insert(questions: Questions)

    @Query("SELECT * FROM questions")
    suspend fun getAllUsers(): List<User>
}


interface Rep {
    suspend fun  insert(note: NoteModel)
}


class RepImpl @Inject constructor(
    private val noteDao: NoteDao,
//    private val questionDao: QuestionDao,
//    private val summaryDao: SummaryDao
) : Rep{
    override suspend fun insert(note: NoteModel) {
        withContext(IO) {
            noteDao.insert(note)
        }
    }
//    val note = noteDao.getAllNotes()
//
//    fun getNewNoteID() = noteDao.getNewNoteID()
//
//    suspend fun insertNote(note: NoteRecord){
//        noteDao.insert(note)
//    }
//
//    suspend fun updateNote(note: NoteRecord){
//        noteDao.update(note)
//    }

}



object Graph {
    lateinit var db:AppDatabase
        private set

    val rep by lazy {
        RepImpl(
            noteDao = db.noteDao,
//            summaryDao = db.summaryDao,
//            questionDao = db.questionDao
        )
    }


    fun provide(context: Context) {
        try {
            Log.d("Graph", "Initializing database")
            db = AppDatabase.getDatabase(context)
            Log.d("Graph", "Database initialized: $db")
        } catch (e: Exception) {
            Log.e("Graph", "Error initializing database", e)
            throw e
        }
    }



}


class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao: NoteDao = AppDatabase.getDatabase(application).noteDao
//    private val QuestionDao: QuestionDao = AppDatabase.getDatabase(application).questionDao
//    private val SummaryDao: SummaryDao = AppDatabase.getDatabase(application).summaryDao

    fun insert(note: NoteModel) = viewModelScope.launch(Dispatchers.IO) {
        noteDao.insert(note)
    }
}