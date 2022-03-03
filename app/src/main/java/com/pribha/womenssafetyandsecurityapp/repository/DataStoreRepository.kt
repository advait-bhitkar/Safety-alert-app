package com.pribha.womenssafetyandsecurityapp.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.createDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.google.firebase.firestore.remote.Datastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.prefs.Preferences


const val PREFERENCE_NAME = "my_preference"

class DataStoreRepository (context: Context){

    private object PreferenceKeys{
        val uid = preferencesKey<String>("uid")
        val phoneNo = preferencesKey<String>("phone_no")
        val isFirstAppLaunch = preferencesKey<Boolean>("isFirstAppLaunch")

        val allPermissionGranted = preferencesKey<Boolean>("allPermissionGranted")


    }

    private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    suspend fun saveUidToDataStore(uid: String){

        dataStore.edit { preference->
            preference[PreferenceKeys.uid] = uid
        }
    }

    val readUidFromDataStore: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException){
                Log.d("Datastore", exception.message.toString())
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map { preference ->
            val myUid = preference[PreferenceKeys.uid] ?: "none"
            myUid
        }


    suspend fun savePhoneNoToDataStore(phoneNo: String){

        dataStore.edit { preference->
            preference[PreferenceKeys.phoneNo] = phoneNo
        }
    }


    val readPhoneNoFromDataStore: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException){
                Log.d("Datastore", exception.message.toString())
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map { preference ->
            val myUid = preference[PreferenceKeys.phoneNo] ?: "none"
            myUid
        }


    suspend fun saveNameToDataStore(phoneNo: String){

        dataStore.edit { preference->
            preference[PreferenceKeys.phoneNo] = phoneNo
        }
    }



    val readIsFirstLaunchFromDataStore: Flow<Boolean> = dataStore.data
            .catch { exception ->
                if (exception is IOException){
                    Log.d("Datastore", exception.message.toString())
                    emit(emptyPreferences())
                }else{
                    throw exception
                }
            }
            .map { preference ->
                val myUid = preference[PreferenceKeys.isFirstAppLaunch] ?: true
                myUid
            }


    suspend fun saveIsFirstLaunchToDataStore(isFirstLaunch: Boolean){

        dataStore.edit { preference->
            preference[PreferenceKeys.isFirstAppLaunch] = isFirstLaunch
        }
    }




    val readAllPermissionsGrantedFromDataStore: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException){
                Log.d("Datastore", exception.message.toString())
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map { preference ->
            val myUid = preference[PreferenceKeys.allPermissionGranted] ?: false
            myUid
        }


    suspend fun saveAllPermissionGrantedToDataStore(allPermissionGranted: Boolean){

        dataStore.edit { preference->
            preference[PreferenceKeys.allPermissionGranted] = allPermissionGranted
        }
    }







}