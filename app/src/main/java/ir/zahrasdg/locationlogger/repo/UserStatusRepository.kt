package ir.zahrasdg.locationlogger.repo

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import ir.zahrasdg.locationlogger.model.UserStatus
import ir.zahrasdg.locationlogger.model.UserStatusDao

class UserStatusRepository(private val userStatusDao: UserStatusDao) {

    val allWords: LiveData<List<UserStatus>> = userStatusDao.getAllStatuses()

    @WorkerThread
    suspend fun insert(userStatus: UserStatus) {
        userStatusDao.insert(userStatus)
    }
}