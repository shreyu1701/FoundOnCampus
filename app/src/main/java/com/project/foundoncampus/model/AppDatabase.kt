package com.project.foundoncampus.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.project.foundoncampus.utils.Converters

@Database(
    entities = [
        ListingEntity::class,
        UserEntity::class,
        ProfileEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun listingDao(): ListingDao
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `profiles` (
                      `userId` TEXT NOT NULL,
                      `fullName` TEXT NOT NULL,
                      `phone` TEXT,
                      `studentId` TEXT,
                      `department` TEXT,
                      `avatarUri` TEXT,
                      `updatedAt` INTEGER NOT NULL,
                      PRIMARY KEY(`userId`)
                    )
                    """.trimIndent()
                )
                // INTEGER column matches Date? via converter (epoch millis)
                try { db.execSQL("ALTER TABLE `listings` ADD COLUMN `claimedDate` INTEGER") } catch (_: Throwable) {}
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "found_on_campus_db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
