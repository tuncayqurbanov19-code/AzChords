package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Comment
import com.example.data.model.Favorite
import com.example.data.model.Song
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long
}

@Dao
interface SongDao {
    @Query("SELECT * FROM songs WHERE isApproved = 1 ORDER BY title ASC")
    fun getApprovedSongsFlow(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE isApproved = 1 ORDER BY views DESC LIMIT 10")
    fun getTrendingSongsFlow(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE isApproved = 0 ORDER BY id DESC")
    fun getPendingSongsFlow(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE id = :id LIMIT 1")
    suspend fun getSongById(id: Int): Song?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song): Long

    @Update
    suspend fun updateSong(song: Song)

    @Query("UPDATE songs SET views = views + 1 WHERE id = :id")
    suspend fun incrementViews(id: Int)

    @Query("UPDATE songs SET isApproved = 1 WHERE id = :id")
    suspend fun approveSong(id: Int)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: Int)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesForUserFlow(userId: Int): Flow<List<Favorite>>

    @Query("SELECT * FROM favorites WHERE userId = :userId AND songId = :songId LIMIT 1")
    suspend fun getFavorite(userId: Int, songId: Int): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE userId = :userId AND songId = :songId")
    suspend fun removeFavorite(userId: Int, songId: Int)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE songId = :songId ORDER BY timestamp DESC")
    fun getCommentsForSongFlow(songId: Int): Flow<List<Comment>>

    @Query("SELECT * FROM comments")
    fun getAllCommentsFlow(): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)
}
