package com.merttoptas.cointracker.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.merttoptas.cointracker.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(Constants.KEY_DATA_STORE_SETTINGS)

class DataStoreManager(context: Context) {
    private object PreferencesKeys {
        val USER_LOGIN = booleanPreferencesKey(Constants.KEY_USER_TOKEN)
    }

    private val dataStore = context.dataStore

    val userLogin: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_LOGIN] ?: false
    }

    suspend fun updateUserLogin(userLogin: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferencesKeys.USER_LOGIN] = userLogin
        }
    }
}