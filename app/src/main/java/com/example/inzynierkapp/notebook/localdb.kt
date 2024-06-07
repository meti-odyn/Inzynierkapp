package com.example.inzynierkapp.notebook

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.inzynierkapp.login.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject


@Entity(tableName = "notes")
data class NoteRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String?,
    val content: String?,
    val data: Date,
)


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
    entity = NoteRecord::class,
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




@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteRecord): Long


    @Update
    suspend fun update(note:NoteRecord)

    @Query ("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<NoteRecord>>

    @Query ("SELECT id FROM notes ORDER BY data DESC LIMIT 1")
    fun getNewNoteID(): Int
}


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






@Database(entities = [NoteRecord::class, Summary::class, Questions::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao
    abstract val summaryDao: SummaryDao
    abstract val questionDao: QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}



interface Rep {
    suspend fun  insert(note: NoteRecord)
}


class RepImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val questionDao: QuestionDao,
    private val summaryDao: SummaryDao
) : Rep{
    override suspend fun insert(note: NoteRecord) {
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
            summaryDao = db.summaryDao,
            questionDao = db.questionDao
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
    private val QuestionDao: QuestionDao = AppDatabase.getDatabase(application).questionDao
    private val SummaryDao: SummaryDao = AppDatabase.getDatabase(application).summaryDao

    fun insert(note: NoteRecord) = viewModelScope.launch(Dispatchers.IO) {
        noteDao.insert(note)
    }
}