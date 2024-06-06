package com.example.inzynierkapp.notebook
import android.content.Context
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.inzynierkapp.User


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



//
//@Dao
//interface NoteDao {
//    @Insert
//    suspend fun insert(noteRecord: NoteRecord)
//    @Update
//    suspend fun update(noteRecord: NoteRecord)
//    @Query("SELECT * FROM notes")
//    suspend fun getAllUsers(): List<User>
//}
//
//@Dao
//interface SummaryDao {
//    @Insert
//    suspend fun insert(summary: Summary)
//
//    @Query("SELECT * FROM summaries")
//    suspend fun getAllUsers(): List<User>
//}
//
//interface QuestionDao {
//    @Insert
//    suspend fun insert(questions: Questions)
//
//    @Query("SELECT * FROM questions")
//    suspend fun getAllUsers(): List<User>
//}
//
//
//
//
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

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(noteRecord: NoteRecord)
    @Update
    suspend fun update(noteRecord: NoteRecord)
    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<NoteRecord>
}

@Dao
interface SummaryDao {
    @Insert
    suspend fun insert(summary: Summary)
    @Query("SELECT * FROM summaries")
    suspend fun getAllSummaries(): List<Summary>
}

@Dao
interface QuestionDao {
    @Insert
    suspend fun insert(questions: Questions)
    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<Questions>
}

@Dao
interface UserDao {
    @Entity(tableName = "users")
    data class User(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val firstName: String?,
        val lastName: String?,
        val email: String
    )
    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?
}
@Database(entities = [User::class, NoteRecord::class, Questions::class, Summary::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun summaryDao(): SummaryDao
    abstract fun questionDao(): QuestionDao


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