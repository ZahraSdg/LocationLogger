package ir.zahrasdg.locationlogger.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserStatus::class], version = 1)
abstract class UserStatusRoomDataBase : RoomDatabase() {

    abstract fun userStatusDao(): UserStatusDao

    companion object {
        @Volatile
        private var INSTANCE: UserStatusRoomDataBase? = null

        fun getDatabase(context: Context): UserStatusRoomDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserStatusRoomDataBase::class.java,
                    "user_status_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}