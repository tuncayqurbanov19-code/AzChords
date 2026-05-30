package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val isAdmin: Boolean = false
)

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist: String,
    val chordsAndLyrics: String, // String content in bracket notation: e.g. "[Am]Sarı [C]gəlin [G]gözəlim"
    val capo: Int = 0,
    val views: Int = 0,
    val isApproved: Boolean = false,
    val submittedByUsername: String = "Sistem"
)

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val songId: Int
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val songId: Int,
    val username: String,
    val commentText: String,
    val timestamp: Long = System.currentTimeMillis()
)
