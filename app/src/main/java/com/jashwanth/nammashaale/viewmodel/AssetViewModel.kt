package com.jashwanth.nammashaale.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jashwanth.nammashaale.data.Asset
import com.jashwanth.nammashaale.data.AssetCondition
import com.jashwanth.nammashaale.data.AssetRepository
import com.jashwanth.nammashaale.database.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AssetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AssetRepository

    val allAssets: Flow<List<Asset>>

    init {
        val assetDao = AppDatabase.getDatabase(application).assetDao()
        repository = AssetRepository(assetDao)
        allAssets = repository.allAssets
    }

    fun insertAsset(asset: Asset) {
        viewModelScope.launch {
            repository.insert(asset)
        }
    }

    fun updateAssetCondition(assetId: Int, newCondition: AssetCondition) {
        viewModelScope.launch {
            // We need to add this to repository too
            val assetDao = AppDatabase.getDatabase(getApplication()).assetDao()
            assetDao.updateAssetCondition(assetId, newCondition)
        }
    }
}
