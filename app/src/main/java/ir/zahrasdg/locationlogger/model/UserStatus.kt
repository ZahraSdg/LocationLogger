package ir.zahrasdg.locationlogger.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status_table")
data class UserStatus(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    @ColumnInfo(name = "timeStamp")
    val timeStamp: Long
)