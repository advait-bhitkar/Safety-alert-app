package com.pribha.womenssafetyandsecurityapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pribha.womenssafetyandsecurityapp.repository.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataStoreViewModel(application: Application): AndroidViewModel(application) {

    private val repository = DataStoreRepository(application)

    val readIsFirstLaunchFromDataStore =  repository.readIsFirstLaunchFromDataStore.asLiveData()




     fun saveIsFirstLaunchToDataStore(isFirstLaunch: Boolean) = viewModelScope.launch(Dispatchers.IO) {
         repository.saveIsFirstLaunchToDataStore(isFirstLaunch)

     }



    val readAllPermissionGrantedFromDataStore =  repository.readAllPermissionsGrantedFromDataStore.asLiveData()

    fun saveAllPermissionGrantedToDataStore(allPermissionGranted: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveAllPermissionGrantedToDataStore(allPermissionGranted)

    }

}