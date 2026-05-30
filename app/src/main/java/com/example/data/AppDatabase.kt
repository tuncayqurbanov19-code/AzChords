package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CommentDao
import com.example.data.dao.FavoriteDao
import com.example.data.dao.SongDao
import com.example.data.dao.UserDao
import com.example.data.model.Comment
import com.example.data.model.Favorite
import com.example.data.model.Song
import com.example.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Song::class, Favorite::class, Comment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun songDao(): SongDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "azchords_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(db: AppDatabase) {
            val userDao = db.userDao()
            val songDao = db.songDao()
            val commentDao = db.commentDao()

            // Seed Admin User (Email: admin@azchords.com, Password: admin2026)
            userDao.insertUser(
                User(
                    username = "admin",
                    email = "admin@azchords.com",
                    passwordHash = "admin2026",
                    isAdmin = true
                )
            )

            // Seed a Standard User
            userDao.insertUser(
                User(
                    username = "musiqi_sever",
                    email = "istifadeci@azchords.com",
                    passwordHash = "user123",
                    isAdmin = false
                )
            )

            // Seed initial beautiful Azerbaijan Songs
            val song1Id = songDao.insertSong(
                Song(
                    title = "Sarı Gəlin",
                    artist = "Azərbaycan Xalq Mahnısı",
                    chordsAndLyrics = """
[Am]Saçın ucun [Dm]hörməzlər
[G]Gülü sulu [C]dərməzlər
[Dm]Sarı gəlin [Am]

[Am]Bu sevda nə [Dm]sevdadır
[G]Səni mənə [C]verməzlər
[Dm]Neylim aman, [Am]aman
[F]Sarı gəlin [Am]

[Am]Bu dərənin [Dm]uzunu,
[G]Çoban qaytar [C]quzunu, quzunu.
[Dm]Sarı gəlin [Am]

[Am]Neylim aman, [Dm]aman
[F]Sarı gəlin [Am]
                    """.trimIndent(),
                    capo = 2,
                    views = 1250,
                    isApproved = true,
                    submittedByUsername = "Sistem"
                )
            )

            val song2Id = songDao.insertSong(
                Song(
                    title = "Sən Gəlməz Oldun",
                    artist = "Alim Qasımov",
                    chordsAndLyrics = """
[Am]Deirdin baharda [Dm]görüşəcəyik,
[G]Bahar gəldi keçdi, [C]sən gəlməz oldun.
[Dm]Yarpaqlar töküldü, [Am]payız da gəldi,
[F]Sən gəlməz oldun, [E]sən gəlməz oldun.

[Am]Sən gəlməz [Dm]oldun,
[G]Sən gəlməz [C]oldun,
[Dm]Sən gəlməz [Am]oldun,
[F]Yarım sən gəlməz [E]oldun.

[Am]Gözlərim caddədə, [Dm]qulağım səsdə,
[G]Təbibim, can üstə [C]sən gəlməz oldun.
[Dm]Yarpaqlar töküldü, [Am]payız da gəldi,
[F]Sən gəlməz oldun, [E]sən gəlməz oldun.
                    """.trimIndent(),
                    capo = 1,
                    views = 2840,
                    isApproved = true,
                    submittedByUsername = "Sistem"
                )
            )

            val song3Id = songDao.insertSong(
                Song(
                    title = "Azərbaycan",
                    artist = "Müslüm Maqomayev",
                    chordsAndLyrics = """
[G]Çox keçmişəm bu [C]dağlardan,
[D]Durna gözlü [G]bulaqlardan,
[C]Eşitmişəm [G]uzaqlardan,
[D]Sakit axan [G]Arazı.

[G]Azərbaycan, [C]Azərbaycan!
[D]Odlar yurdu [G]Azərbaycan!

[G]Bir tərəfi [C]bəhri-Xəzər,
[D]Yaşıl qoynu [G]göz oxşar,
[C]Sənin şanın [G]böyük olar,
[D]Ana yurdum, [G]gözəlim.
                    """.trimIndent(),
                    capo = 0,
                    views = 3500,
                    isApproved = true,
                    submittedByUsername = "Sistem"
                )
            )

            val song4Id = songDao.insertSong(
                Song(
                    title = "Külək",
                    artist = "Mirzə Babayev",
                    chordsAndLyrics = """
[Am]Yenə əsir [Dm]soyuq külək,
[G]Yenə sızlar [C]məyus ürək.
[Dm]Sənsiz necə [Am]gülüm deyim,
[E]Səni sevirəm, [Am]gözəlim, deyim.

[Am]Külək, sən [Dm]əs, yarım gəlsin,
[G]Onun ətri [C]bura gəlsin.
[Dm]Öpüm onun [Am]gözlərindən,
[E]O şirin [Am]sözlərindən.
                    """.trimIndent(),
                    capo = 3,
                    views = 920,
                    isApproved = true,
                    submittedByUsername = "Sistem"
                )
            )

            // Seed initial comment
            commentDao.insertComment(
                Comment(
                    songId = song2Id.toInt(),
                    username = "musiqi_sever",
                    commentText = "Çox möhtəşəm bir əsərdir, akordları da çox dəqiq çıxarılıb. Təşəkkürlər!",
                    timestamp = System.currentTimeMillis() - 7200000
                )
            )
        }
    }
}
