package com.jashwanth.nammashaale.data

import com.jashwanth.nammashaale.database.AssetDao
import kotlinx.coroutines.flow.Flow

class AssetRepository(private val assetDao: AssetDao) {

    val allAssets: Flow<List<Asset>> = assetDao.getAllAssets()

    suspend fun insert(asset: Asset) {
        assetDao.insert(asset)
    }

    suspend fun updateAssetCondition(assetId: Int, newCondition: AssetCondition) {
        assetDao.updateAssetCondition(assetId, newCondition)
    }
}
