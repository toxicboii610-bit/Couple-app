package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. Entities

@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String,
    val note: String,
    val imageUrl: String = "",
    val musicType: String = "Lofi Rain",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "Nikki" or "Suraj"
    val text: String,
    val stickerName: String = "", // empty if text message
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "period_logs")
data class PeriodLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: Long, // timestamp
    val calculatedNextDate: Long, // timestamp
    val notes: String = "",
    val loggedBy: String = "Suraj"
)

@Entity(tableName = "wrong_notes")
data class WrongNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reaction: String = "" // reaction character: 😭, 💀, 🫠, ❤️ or empty
)

// 2. DAOs

@Dao
interface UniverseDao {
    // Memories
    @Query("SELECT * FROM memories ORDER BY timestamp ASC")
    fun getAllMemoriesStateFlow(): Flow<List<Memory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: Memory)

    @Delete
    suspend fun deleteMemory(memory: Memory)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    // Period Logs
    @Query("SELECT * FROM period_logs ORDER BY startDate DESC LIMIT 1")
    fun getLatestPeriod(): Flow<PeriodLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(log: PeriodLog)

    // Wrong Notes
    @Query("SELECT * FROM wrong_notes ORDER BY timestamp DESC")
    fun getAllWrongNotes(): Flow<List<WrongNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrongNote(note: WrongNote)

    @Delete
    suspend fun deleteWrongNote(note: WrongNote)
}

// 3. Database

@Database(
    entities = [Memory::class, ChatMessage::class, PeriodLog::class, WrongNote::class],
    version = 1,
    exportSchema = false
)
abstract class UniverseDatabase : RoomDatabase() {
    abstract fun dao(): UniverseDao

    companion object {
        @Volatile
        private var INSTANCE: UniverseDatabase? = null

        fun getDatabase(context: Context): UniverseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UniverseDatabase::class.java,
                    "our_universe_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// 4. Repository

class UniverseRepository(private val dao: UniverseDao) {
    val allMemories: Flow<List<Memory>> = dao.getAllMemoriesStateFlow()
    val allMessages: Flow<List<ChatMessage>> = dao.getAllMessages()
    val latestPeriod: Flow<PeriodLog?> = dao.getLatestPeriod()
    val allWrongNotes: Flow<List<WrongNote>> = dao.getAllWrongNotes()

    suspend fun addMemory(memory: Memory) = dao.insertMemory(memory)
    suspend fun removeMemory(memory: Memory) = dao.deleteMemory(memory)

    suspend fun sendMessage(message: ChatMessage) = dao.insertMessage(message)

    suspend fun savePeriod(log: PeriodLog) = dao.insertPeriod(log)

    suspend fun addWrongNote(note: WrongNote) = dao.insertWrongNote(note)
    suspend fun removeWrongNote(note: WrongNote) = dao.deleteWrongNote(note)
}
