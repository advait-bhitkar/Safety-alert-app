package com.pribha.womenssafetyandsecurityapp.onboarding.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pribha.womenssafetyandsecurityapp.repository.DataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application) {

    private val repository = DataStoreRepository(application)

    val readUidFromDataStore = repository.readUidFromDataStore.asLiveData()

    fun saveUidToDataStore (myUid: String) = viewModelScope.launch(Dispatchers.IO){

        repository.saveUidToDataStore(myUid)
    }
}