package ir.zahrasdg.locationlogger.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserStatus::class], version = 1)
abstract class LocationLoggerDataBase : RoomDatabase() {

    abstract fun userStatusDao(): UserStatusDao

    companion object {

        fun getDatabase(context: Context): LocationLoggerDataBase {
            synchronized(this) {
                return Room.databaseBuilder(
                    context.applicationContext,
                    LocationLoggerDataBase::class.java,
                    "location_helper_database"
                ).build()
            }
        }
    }
}