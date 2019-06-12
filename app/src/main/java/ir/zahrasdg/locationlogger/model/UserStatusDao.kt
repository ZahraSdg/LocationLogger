package ir.zahrasdg.locationlogger.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserStatusDao {

    @Query("SELECT * FROM status ORDER BY time_stamp ASC LIMIT :limit OFFSET :offset")
    fun getAllStatuses(limit: Int, offset: Int): LiveData<List<UserStatus>>

    @Query("SELECT * FROM status WHERE id = :id ")
    fun getStatus(id: Int): LiveData<UserStatus>

    @Insert
    suspend fun insert(status: UserStatus): Long

}