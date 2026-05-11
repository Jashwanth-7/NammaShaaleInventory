package com.jashwanth.nammashaale.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemName: String,
    val category: String,
    val serialNumber: String,
    val quantity: Int,
    val purchaseDate: String,
    val location: String, // School Name/Classroom
    val itemPhotoPath: String?,
    val condition: AssetCondition = AssetCondition.WORKING
)

enum class AssetCondition {
    WORKING, // Green
    NEEDS_REPAIR, // Yellow
    BROKEN // Red
}
