package com.jashwanth.nammashaale.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jashwanth.nammashaale.data.Asset
import com.jashwanth.nammashaale.data.AssetCondition
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: Asset)

    @Update
    suspend fun update(asset: Asset)

    @Query("SELECT * FROM assets ORDER BY id DESC")
    fun getAllAssets(): Flow<List<Asset>>

    @Query("UPDATE assets SET condition = :newCondition WHERE id = :assetId")
    suspend fun updateAssetCondition(assetId: Int, newCondition: AssetCondition)
}
