package com.example.inzynierkapp.notebook

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inzynierkapp.login.User
import java.util.Date


@Entity(tableName = "notes")
data class NoteRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val content: String,
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
    @Insert
    suspend fun insert(noteRecord: NoteRecord)

    @Query("SELECT * FROM notes")
    suspend fun getAllUsers(): List<User>
}

@Dao
interface SummaryDao {
    @Insert
    suspend fun insert(summary: Summary)

    @Query("SELECT * FROM summaries")
    suspend fun getAllUsers(): List<User>
}

interface QuestionDao {
    @Insert
    suspend fun insert(questions: Questions)

    @Query("SELECT * FROM questions")
    suspend fun getAllUsers(): List<User>
}




//@Database(entities = [User::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun userDao(): NoteDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "app_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}