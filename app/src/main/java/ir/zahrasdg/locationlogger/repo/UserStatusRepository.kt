package ir.zahrasdg.locationlogger.repo

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import ir.zahrasdg.locationlogger.model.UserStatus
import ir.zahrasdg.locationlogger.model.UserStatusDao

class UserStatusRepository(private val userStatusDao: UserStatusDao) {

    companion object {

        private const val PAGING_LIMIT = 50
    }

    fun loadStatusPage(pageNumber: Int): LiveData<List<UserStatus>> {
        return userStatusDao.getAllStatuses(PAGING_LIMIT, (pageNumber - 1) * PAGING_LIMIT)
    }

    fun loadNewlyInsertedStatus(id: Int): LiveData<UserStatus> {
        return userStatusDao.getStatus(id)
    }

    @WorkerThread
    suspend fun insert(userStatus: UserStatus): Long {
        return userStatusDao.insert(userStatus)
    }
}