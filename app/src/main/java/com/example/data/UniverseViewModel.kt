package com.example.data

import android.app.Application
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UniverseViewModel(application: Application) : AndroidViewModel(application) {
    private val database = UniverseDatabase.getDatabase(application)
    private val repository = UniverseRepository(database.dao())

    // App Navigation and Perspectives
    val currentTab = mutableStateOf("home") // home, constellation, moons, chat, caret, wrongs, letters
    val currentUser = mutableStateOf("Nikki") // Active persona "Nikki" or "Suraj"

    // Music Player State (Synthesized Lofi tone loops)
    val isMusicPlaying = mutableStateOf(false)
    private var audioTrack: AudioTrack? = null
    private var isPlayingLofi = false

    // State flows from database
    val memories: StateFlow<List<Memory>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestPeriodLog: StateFlow<PeriodLog?> = repository.latestPeriod
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val wrongNotes: StateFlow<List<WrongNote>> = repository.allWrongNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic shooting star messages
    private val _currentShootingMessage = MutableStateFlow("Somewhere between the stars, we found home.")
    val currentShootingMessage: StateFlow<String> = _currentShootingMessage

    private val quotePool = listOf(
        "Somewhere between the stars, we found home.",
        "Nikki & Suraj — written in constellations.",
        "Art, chaos, memories, and us.",
        "Every galaxy feels softer with you.",
        "In our universe, love is paint, and we are the canvas.",
        "Late nights, glowing screens, and starry dreams.",
        "With Nikki & Suraj, gravity works in reverse.",
        "Your touch is a soft watercolor splash on my soul."
    )

    init {
        // Prepopulate baseline couple info if database is empty
        viewModelScope.launch(Dispatchers.IO) {
            repository.allMemories.first().let { list ->
                if (list.isEmpty()) {
                    // Prepopulate standard memory events
                    repository.addMemory(Memory(
                        title = "The Beginning",
                        date = "6 October 2024",
                        note = "The magical spark that started our private universe. We looked at each other and knew the stars allied.",
                        imageUrl = "beginning",
                        timestamp = parseDateToMillis("06 Oct 2024")
                    ))
                    repository.addMemory(Memory(
                        title = "First Long Call",
                        date = "15 October 2024",
                        note = "Holding our phones until they got warm, talks about universe, painters, pencils, and late nights.",
                        imageUrl = "call",
                        timestamp = parseDateToMillis("15 Oct 2024")
                    ))
                    repository.addMemory(Memory(
                        title = "First Random Fight",
                        date = "02 November 2024",
                        note = "A little storm in our solar system, but it only made our gravity twice as strong. Nikki smiled first!",
                        imageUrl = "fight",
                        timestamp = parseDateToMillis("02 Nov 2024")
                    ))
                    repository.addMemory(Memory(
                        title = "That One Perfect Day",
                        date = "25 December 2024",
                        note = "Sunlight through the trees, sketches of our dream house, and sharing lofi music in perfect harmony.",
                        imageUrl = "perfect_day",
                        timestamp = parseDateToMillis("25 Dec 2024")
                    ))
                    repository.addMemory(Memory(
                        title = "Still Growing Together",
                        date = "26 May 2026",
                        note = "Looking back, every single frame feels like fine art. Here's to painting a million more memories.",
                        imageUrl = "growing",
                        timestamp = parseDateToMillis("26 May 2026")
                    ))
                }
            }

            repository.allMessages.first().let { list ->
                if (list.isEmpty()) {
                    repository.sendMessage(ChatMessage(sender = "Suraj", text = "Welcome to Our Private Galaxy, Nikki! 🌟"))
                    repository.sendMessage(ChatMessage(sender = "Nikki", text = "It's beautiful Suraj! Look at the orbiting planets! 🪐🎨"))
                    repository.sendMessage(ChatMessage(sender = "Suraj", text = "Built from our memories, lofi beats & late nights." ))
                }
            }

            repository.allWrongNotes.first().let { list ->
                if (list.isEmpty()) {
                    repository.addWrongNote(WrongNote(
                        content = "Suraj forgot to play the ambient music track while we sketched our starry drawings.",
                        reaction = "🫠"
                    ))
                    repository.addWrongNote(WrongNote(
                        content = "Suraj called it 'yellow' when it was clearly sunset gold-ochre watercolor paint!",
                        reaction = "😭"
                    ))
                }
            }

            // Period default log
            repository.latestPeriod.first().let { log ->
                if (log == null) {
                    val calendar = Calendar.getInstance()
                    calendar.set(2026, Calendar.MAY, 15) // Recent baseline
                    val start = calendar.timeInMillis
                    calendar.add(Calendar.DAY_OF_YEAR, 28)
                    val next = calendar.timeInMillis
                    repository.savePeriod(PeriodLog(
                        startDate = start,
                        calculatedNextDate = next,
                        notes = "Quiet week. Take extra care of the artist! ✨",
                        loggedBy = "Suraj"
                    ))
                }
            }
        }

        // Periodic Quotes Loop
        viewModelScope.launch {
            while (true) {
                delay(12000)
                _currentShootingMessage.value = quotePool.random()
            }
        }
    }

    private fun parseDateToMillis(dateStr: String): Long {
        return try {
            val format = SimpleDateFormat("dd MMM yyyy", Locale.US)
            format.parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    // Interactive functions
    fun toggleUser() {
        currentUser.value = if (currentUser.value == "Nikki") "Suraj" else "Nikki"
    }

    // Memories manipulation
    fun createMemory(title: String, note: String, dateStr: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val ts = parseDateToMillis(dateStr)
            repository.addMemory(
                Memory(title = title, date = dateStr, note = note, timestamp = ts)
            )
        }
    }

    fun deleteMemory(memory: Memory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeMemory(memory)
        }
    }

    // Messages manipulation
    fun sendChatMessage(text: String, sticker: String = "") {
        if (text.trim().isEmpty() && sticker.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendMessage(
                ChatMessage(sender = currentUser.value, text = text, stickerName = sticker)
            )
        }
    }

    // Period manipulation
    fun savePeriodStart(dateMillis: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dateMillis
            calendar.add(Calendar.DAY_OF_YEAR, 28) // Estimate 28-day cycle
            val nextDate = calendar.timeInMillis
            repository.savePeriod(
                PeriodLog(startDate = dateMillis, calculatedNextDate = nextDate, loggedBy = currentUser.value)
            )
        }
    }

    // Wrongs manipulation
    fun addWrong(content: String) {
        if (currentUser.value != "Nikki") return // Only Nikki can write!
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWrongNote(WrongNote(content = content))
        }
    }

    fun updateWrongReaction(note: WrongNote, reactionChar: String) {
        // Anyone can express reaction!
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWrongNote(note.copy(reaction = reactionChar))
        }
    }

    fun deleteWrong(note: WrongNote) {
        if (currentUser.value != "Nikki") return // Only Nikki can delete!
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeWrongNote(note)
        }
    }

    // Dynamic countdown data provider
    fun getCountdownToAnniversary(onTick: (days: Long, hours: Long, minutes: Long, seconds: Long, completedYears: Int) -> Unit) {
        viewModelScope.launch {
            while (true) {
                val now = Calendar.getInstance()
                val yearNow = now.get(Calendar.YEAR)

                // Anniversary is Oct 6
                val target = Calendar.getInstance()
                target.set(Calendar.YEAR, yearNow)
                target.set(Calendar.MONTH, Calendar.OCTOBER)
                target.set(Calendar.DAY_OF_MONTH, 6)
                target.set(Calendar.HOUR_OF_DAY, 0)
                target.set(Calendar.MINUTE, 0)
                target.set(Calendar.SECOND, 0)

                if (now.after(target)) {
                    // Target has passed this year, countdown is for next year
                    target.add(Calendar.YEAR, 1)
                }

                // Years completed
                // Start = Oct 6, 2024
                // Today = May 2026. Anniversary of 2025 is completed (1 yr). 2026 will make it 2.
                // Complete calculation based on target year or last anniversary year
                val completedYears = target.get(Calendar.YEAR) - 1 - 2024

                val diff = target.timeInMillis - now.timeInMillis
                if (diff > 0) {
                    val days = diff / (1000 * 60 * 60 * 24)
                    val hours = (diff / (1000 * 60 * 60)) % 24
                    val minutes = (diff / (1000 * 60)) % 60
                    val seconds = (diff / 1000) % 60
                    onTick(days, hours, minutes, seconds, completedYears)
                } else {
                    onTick(0, 0, 0, 0, completedYears + 1)
                }
                delay(1000)
            }
        }
    }

    // ----------------------------------------------------
    // Synthesized Lofi Tone Ambient Loop
    // ----------------------------------------------------
    fun toggleAmbientMusic() {
        if (isMusicPlaying.value) {
            stopAmbientMusic()
        } else {
            startAmbientMusic()
        }
    }

    private fun startAmbientMusic() {
        isMusicPlaying.value = true
        isPlayingLofi = true
        viewModelScope.launch(Dispatchers.IO) {
            val sampleRate = 8000
            val numSamples = 24000
            val sound = DoubleArray(numSamples)
            val generatedSnd = ByteArray(2 * numSamples)

            // Synthesize repeating serene ambient chord (E.g. Amaj9 cosmic tone with relaxing modulation)
            // Frequencies of Amaj9 chord: A (220Hz), C# (277.18Hz), E (329.63Hz), G# (415.30Hz), B (493.88Hz)
            val freqs = doubleArrayOf(220.0, 277.18, 329.63, 415.30, 493.88)

            for (i in 0 until numSamples) {
                var sum = 0.0
                val t = i.toDouble() / sampleRate
                // Combine sin waves with relaxing slow volume LFO modulation
                val lfo = 0.6 + 0.4 * kotlin.math.sin(2.0 * kotlin.math.PI * 0.15 * t)
                for (f in freqs) {
                    sum += kotlin.math.sin(2.0 * kotlin.math.PI * f * t)
                }
                sum /= freqs.size
                sound[i] = sum * lfo // scale with lfo filter
            }

            var idx = 0
            for (dVal in sound) {
                val valShort = (dVal * 10000).toInt().toShort()
                generatedSnd[idx++] = (valShort.toInt() and 0x00ff).toByte()
                generatedSnd[idx++] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
            }

            try {
                audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    generatedSnd.size,
                    AudioTrack.MODE_STATIC
                )
                audioTrack?.write(generatedSnd, 0, generatedSnd.size)
                audioTrack?.setLoopPoints(0, numSamples, -1) // repeat indefinitely
                audioTrack?.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopAmbientMusic() {
        isMusicPlaying.value = false
        isPlayingLofi = false
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAmbientMusic()
    }
}
