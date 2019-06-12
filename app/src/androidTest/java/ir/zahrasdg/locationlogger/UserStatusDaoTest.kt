package ir.zahrasdg.locationlogger

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import ir.zahrasdg.locationlogger.model.UserStatus
import ir.zahrasdg.locationlogger.model.UserStatusDao
import ir.zahrasdg.locationlogger.model.UserStatusRoomDataBase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule



@RunWith(AndroidJUnit4::class)
open class UserStatusDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: UserStatusRoomDataBase
    private lateinit var dao: UserStatusDao

    @Before
    fun init() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), UserStatusRoomDataBase::class.java)
            .build()
        dao = db.userStatusDao()
    }

    @After
    fun closeDb() {
        db.close()
        dao = db.userStatusDao()
    }

    @Test
    fun insertUserStatus() {
        val status = UserStatus(1, 2.0, 3.0, 1)
        runBlocking {
            dao.insert(status)
        }

        val inserted = LiveDataTestUtil.getValue(dao.getStatus(1))

        Assert.assertNotEquals(inserted, null)
    }
}
