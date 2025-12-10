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
        ProfileEntity::class,
        AccountSettingsEntity::class,
    ],
    version = 5,              // keep 5
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun listingDao(): ListingDao
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun accountSettingsDao(): AccountSettingsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // 2 → 3: create 'profiles' + add 'claimedDate' (nullable) to 'listings'
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
                try { db.execSQL("ALTER TABLE `listings` ADD COLUMN `claimedDate` INTEGER") } catch (_: Throwable) {}
            }
        }

        // 3 → 4: create 'account_settings'
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `account_settings` (
                      `userEmail` TEXT NOT NULL,
                      `displayName` TEXT,
                      `emailNotifications` INTEGER NOT NULL,
                      `pushNotifications` INTEGER NOT NULL,
                      `darkModeEnabled` INTEGER NOT NULL,
                      `updatedAt` INTEGER NOT NULL,
                      PRIMARY KEY(`userEmail`)
                    )
                    """.trimIndent()
                )
            }
        }

        // 4 → 5: ensure 'profiles' and 'account_settings' exist AND patch 'listings'
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Some dev v4 DBs missed earlier migrations; recreate idempotently.
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

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `account_settings` (
                      `userEmail` TEXT NOT NULL,
                      `displayName` TEXT,
                      `emailNotifications` INTEGER NOT NULL,
                      `pushNotifications` INTEGER NOT NULL,
                      `darkModeEnabled` INTEGER NOT NULL,
                      `updatedAt` INTEGER NOT NULL,
                      PRIMARY KEY(`userEmail`)
                    )
                    """.trimIndent()
                )

                // Patch 'listings' to current entity shape
                try { db.execSQL("ALTER TABLE `listings` ADD COLUMN `campus` TEXT NOT NULL DEFAULT ''") } catch (_: Throwable) {}
                try { db.execSQL("ALTER TABLE `listings` ADD COLUMN `location` TEXT NOT NULL DEFAULT ''") } catch (_: Throwable) {}
                try { db.execSQL("ALTER TABLE `listings` ADD COLUMN `imageUrl` TEXT") } catch (_: Throwable) {}
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
                    .addMigrations(
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5
                    )
                    // Only wipe super-old v1 dev DBs (if any)
                    .fallbackToDestructiveMigrationFrom(1)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
