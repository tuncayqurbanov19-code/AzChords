package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.Comment
import com.example.data.model.Favorite
import com.example.data.model.Song
import com.example.data.model.User
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    data class SongDetail(val songId: Int) : Screen()
    object AddSong : Screen()
    object AdminPanel : Screen()
    object Account : Screen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AppRepository(
            database.userDao(),
            database.songDao(),
            database.favoriteDao(),
            database.commentDao()
        )
    }

    // Auth Sessions & States
    var currentUser by mutableStateOf<User?>(null)
        private set

    var authErrorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    // Navigation & Layout States
    var currentScreen by mutableStateOf<Screen>(Screen.Home)
        private set

    private val screenStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        screenStack.add(currentScreen)
        currentScreen = screen
        
        // When opening a song detail, increment views and reset transposing
        if (screen is Screen.SongDetail) {
            selectedSong = null // reset immediately to avoid showing stale song details during loading
            viewModelScope.launch {
                repository.incrementViews(screen.songId)
                selectedSong = repository.getSongById(screen.songId)
                activeTransposition = 0
                loadCommentsForSong(screen.songId)
            }
        }
    }

    fun goBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            currentScreen = screenStack.removeAt(screenStack.size - 1)
            return true
        }
        return false
    }

    // Search Query State
    val searchQuery = MutableStateFlow("")

    // Raw streams from DB
    private val rawApprovedSongs = repository.approvedSongs
    val trendingSongs: StateFlow<List<Song>> = repository.trendingSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val pendingSongs: StateFlow<List<Song>> = repository.pendingSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined search results
    val approvedSongs: StateFlow<List<Song>> = combine(rawApprovedSongs, searchQuery) { songs, query ->
        if (query.isBlank()) {
            songs
        } else {
            songs.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorite / Repertoire items
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val favoriteSongIds: StateFlow<Set<Int>> = snapshotFlow { currentUser }
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(emptySet())
            } else {
                repository.getFavoritesForUser(user.id)
                    .map { favorites -> favorites.map { it.songId }.toSet() }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Repertoire songs flow
    val repertoireSongs: StateFlow<List<Song>> = combine(
        rawApprovedSongs,
        favoriteSongIds
    ) { songs, favoriteSet ->
        songs.filter { favoriteSet.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Selected Song & Details
    var selectedSong by mutableStateOf<Song?>(null)
        private set

    var activeTransposition by mutableIntStateOf(0)

    val activeComments = MutableStateFlow<List<Comment>>(emptyList())

    // Input States for New Song & Preview
    var inputSongTitle by mutableStateOf("")
    var inputSongArtist by mutableStateOf("")
    var inputSongCapo by mutableStateOf("0")
    var inputSongChordsAndLyrics by mutableStateOf("")

    // Admin Edit state
    var editingSongId by mutableStateOf<Int?>(null)
    var editSongTitle by mutableStateOf("")
    var editSongArtist by mutableStateOf("")
    var editSongCapo by mutableStateOf("0")
    var editSongChordsAndLyrics by mutableStateOf("")

    // Load active comments
    fun loadCommentsForSong(songId: Int) {
        viewModelScope.launch {
            repository.getCommentsForSong(songId).collect {
                activeComments.value = it
            }
        }
    }

    // Post comment logic
    fun postComment(songId: Int, text: String) {
        val user = currentUser
        if (user == null) {
            authErrorMessage = "Rəy yazmaq üçün qeydiyyatdan keçməlisiniz."
            return
        }
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.postComment(songId, user.username, text)
            // Reload
            loadCommentsForSong(songId)
        }
    }

    // Toggle Favorite / Repertoire
    fun toggleFavorite(songId: Int) {
        val user = currentUser
        if (user == null) {
            navigateTo(Screen.Account)
            return
        }
        viewModelScope.launch {
            val isFav = repository.isFavorite(user.id, songId)
            if (isFav) {
                repository.removeFavorite(user.id, songId)
            } else {
                repository.addFavorite(user.id, songId)
            }
            // Trigger refresh
            selectedSong = repository.getSongById(songId)
        }
    }

    fun isFavoriteSong(songId: Int): Boolean {
        return favoriteSongIds.value.contains(songId)
    }

    // Submit Song
    fun submitSong(onCompleted: () -> Unit) {
        val user = currentUser
        if (user == null) {
            authErrorMessage = "Mahnı əlavə etmək üçün daxil olmalısınız."
            return
        }

        if (inputSongTitle.isBlank() || inputSongArtist.isBlank() || inputSongChordsAndLyrics.isBlank()) {
            authErrorMessage = "Lütfən bütün sətirləri doldurun."
            return
        }

        val capoVal = inputSongCapo.toIntOrNull() ?: 0

        viewModelScope.launch {
            val isApprovedInitially = user.isAdmin // Admin submissions are approved instantly
            val song = Song(
                title = inputSongTitle.trim(),
                artist = inputSongArtist.trim(),
                chordsAndLyrics = inputSongChordsAndLyrics.trim(),
                capo = capoVal,
                isApproved = isApprovedInitially,
                submittedByUsername = user.username
            )

            repository.insertSong(song)
            
            // Clear inputs
            inputSongTitle = ""
            inputSongArtist = ""
            inputSongCapo = "0"
            inputSongChordsAndLyrics = ""
            
            successMessage = if (isApprovedInitially) {
                "Mahnı portalda yerləşdirildi!"
            } else {
                "Mahnı təsdiq üçün admin panelinə göndərildi!"
            }
            onCompleted()
        }
    }

    // Register / Authenticate Users
    fun register(usernameInput: String, emailInput: String, passwordInput: String) {
        if (usernameInput.isBlank() || emailInput.isBlank() || passwordInput.isBlank()) {
            authErrorMessage = "Bütün sahələri doldurun."
            return
        }

        viewModelScope.launch {
            val existingEmail = repository.getUserByEmail(emailInput.trim())
            if (existingEmail != null) {
                authErrorMessage = "Bu e-mail artıq qeydiyyatdan keçib."
                return@launch
            }

            val existingUser = repository.getUserByUsername(usernameInput.trim())
            if (existingUser != null) {
                authErrorMessage = "Bu istifadəçi adı artıq götürülüb."
                return@launch
            }

            val newUser = User(
                username = usernameInput.trim(),
                email = emailInput.trim(),
                passwordHash = passwordInput, // simpler for local simulated credentials
                isAdmin = false
            )

            val id = repository.registerUser(newUser)
            currentUser = newUser.copy(id = id.toInt())
            authErrorMessage = null
            successMessage = "Qeydiyyat uğurla tamamlandı!"
        }
    }

    fun login(emailInput: String, passwordInput: String) {
        if (emailInput.isBlank() || passwordInput.isBlank()) {
            authErrorMessage = "E-mail və şifrəni doldurun."
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByEmail(emailInput.trim())
            if (user == null || user.passwordHash != passwordInput) {
                authErrorMessage = "E-mail və ya şifrə yanlışdır."
                return@launch
            }

            currentUser = user
            authErrorMessage = null
            successMessage = "Xoş gəldiniz, ${user.username}!"
        }
    }

    fun logout() {
        currentUser = null
        currentScreen = Screen.Home
        successMessage = "Sistemdən çıxış edildi."
    }

    // Admin functions
    fun approveSong(songId: Int) {
        if (currentUser?.isAdmin != true) return
        viewModelScope.launch {
            repository.approveSong(songId)
            successMessage = "Mahnı uğurla təsdiq olundu və yerləşdirildi!"
        }
    }

    fun deleteSong(songId: Int) {
        if (currentUser?.isAdmin != true) return
        viewModelScope.launch {
            repository.deleteSong(songId)
            successMessage = "Mahnı silindi."
            if (currentScreen is Screen.SongDetail && (currentScreen as Screen.SongDetail).songId == songId) {
                currentScreen = Screen.Home
            }
        }
    }

    fun startEditingSong(song: Song) {
        if (currentUser?.isAdmin != true) return
        editingSongId = song.id
        editSongTitle = song.title
        editSongArtist = song.artist
        editSongCapo = song.capo.toString()
        editSongChordsAndLyrics = song.chordsAndLyrics
    }

    fun saveEditedSong(onCompleted: () -> Unit) {
        if (currentUser?.isAdmin != true) return
        val id = editingSongId ?: return
        val capoVal = editSongCapo.toIntOrNull() ?: 0

        viewModelScope.launch {
            val existing = repository.getSongById(id)
            if (existing != null) {
                val updated = existing.copy(
                    title = editSongTitle.trim(),
                    artist = editSongArtist.trim(),
                    chordsAndLyrics = editSongChordsAndLyrics.trim(),
                    capo = capoVal,
                    isApproved = true // automatic approval on admin save
                )
                repository.updateSong(updated)
                editingSongId = null
                successMessage = "Mahnı dəyişiklikləri qeydə alındı!"
                // Refresh if currently on detail page
                if (selectedSong?.id == id) {
                    selectedSong = updated
                }
                onCompleted()
            }
        }
    }
}
