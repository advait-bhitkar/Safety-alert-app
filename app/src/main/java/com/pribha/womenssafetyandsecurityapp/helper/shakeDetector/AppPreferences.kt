package com.pribha.womenssafetyandsecurityapp.helper.shakeDetector

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class AppPreferences(context: Context?) {
    private val sharedPreferences: SharedPreferences
    fun putString(key: String?, value: String?) {
        try {
            val prefEdit = sharedPreferences.edit()
            prefEdit.putString(key, value)
            prefEdit.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(ClassCastException::class)
    fun getString(key: String?, defValue: String?): String? {
        return sharedPreferences.getString(key, defValue)
    }

    fun putInt(key: String?, value: Int?) {
        try {
            val prefEdit = sharedPreferences.edit()
            prefEdit.putInt(key, value!!)
            prefEdit.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(ClassCastException::class)
    fun getInt(key: String?, defValue: Int?): Int {
        return sharedPreferences.getInt(key, defValue!!)
    }

    fun putLong(key: String?, value: Long?) {
        try {
            val prefEdit = sharedPreferences.edit()
            prefEdit.putLong(key, value!!)
            prefEdit.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(ClassCastException::class)
    fun getLong(key: String?, defValue: Long?): Long {
        return sharedPreferences.getLong(key, defValue!!)
    }

    fun putBoolean(key: String?, defValue: Boolean?) {
        try {
            val prefEdit = sharedPreferences.edit()
            prefEdit.putBoolean(key, defValue!!)
            prefEdit.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(ClassCastException::class)
    fun getBoolean(key: String?, defValue: Boolean?): Boolean {
        return sharedPreferences.getBoolean(key, defValue!!)
    }

    fun putFloat(key: String?, defValue: Float?) {
        try {
            val prefEdit = sharedPreferences.edit()
            prefEdit.putFloat(key, defValue!!)
            prefEdit.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(ClassCastException::class)
    fun getFloat(key: String?, defValue: Float?): Float {
        return sharedPreferences.getFloat(key, defValue!!)
    }

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }
}
