package com.example.data.repository

import com.example.data.dao.CommentDao
import com.example.data.dao.FavoriteDao
import com.example.data.dao.SongDao
import com.example.data.dao.UserDao
import com.example.data.model.Comment
import com.example.data.model.Favorite
import com.example.data.model.Song
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val songDao: SongDao,
    private val favoriteDao: FavoriteDao,
    private val commentDao: CommentDao
) {
    // Songs
    val approvedSongs: Flow<List<Song>> = songDao.getApprovedSongsFlow()
    val trendingSongs: Flow<List<Song>> = songDao.getTrendingSongsFlow()
    val pendingSongs: Flow<List<Song>> = songDao.getPendingSongsFlow()

    suspend fun getSongById(id: Int): Song? {
        return songDao.getSongById(id)
    }

    suspend fun insertSong(song: Song): Long {
        return songDao.insertSong(song)
    }

    suspend fun updateSong(song: Song) {
        songDao.updateSong(song)
    }

    suspend fun incrementViews(id: Int) {
        songDao.incrementViews(id)
    }

    suspend fun approveSong(id: Int) {
        songDao.approveSong(id)
    }

    suspend fun deleteSong(id: Int) {
        songDao.deleteSong(id)
    }

    // Users
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun registerUser(user: User): Long {
        return userDao.insertUser(user)
    }

    // Favorites (Repertoire)
    fun getFavoritesForUser(userId: Int): Flow<List<Favorite>> {
        return favoriteDao.getFavoritesForUserFlow(userId)
    }

    suspend fun isFavorite(userId: Int, songId: Int): Boolean {
        return favoriteDao.getFavorite(userId, songId) != null
    }

    suspend fun addFavorite(userId: Int, songId: Int) {
        favoriteDao.addFavorite(Favorite(userId = userId, songId = songId))
    }

    suspend fun removeFavorite(userId: Int, songId: Int) {
        favoriteDao.removeFavorite(userId, songId)
    }

    // Comments
    fun getCommentsForSong(songId: Int): Flow<List<Comment>> {
        return commentDao.getCommentsForSongFlow(songId)
    }

    suspend fun postComment(songId: Int, username: String, commentText: String) {
        commentDao.insertComment(
            Comment(
                songId = songId,
                username = username,
                commentText = commentText
            )
        )
    }
}
