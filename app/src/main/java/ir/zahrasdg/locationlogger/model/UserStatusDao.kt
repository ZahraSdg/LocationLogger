package ir.zahrasdg.locationlogger.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserStatusDao {

    @Query("SELECT * FROM status_table")
    fun getAllStatuses(): LiveData<List<UserStatus>>

    @Insert
    suspend fun insert(status: UserStatus)

}