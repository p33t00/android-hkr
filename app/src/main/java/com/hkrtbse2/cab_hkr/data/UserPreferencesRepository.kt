package com.hkrtbse2.cab_hkr.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository constructor(private val dataStore: DataStore<Preferences>) {
    // Some keys in the store, referred for this class
    private object PreferenceKeys {
        val USER_LOGIN_STATE = booleanPreferencesKey("user-login-state")
        val USER_SERVICE_URL = stringPreferencesKey("user-service-url")
    }

    val userLoginState: Flow<Boolean> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
        preferences[PreferenceKeys.USER_LOGIN_STATE] ?: false
    }

    val userServiceUrl: Flow<String> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferenceKeys.USER_SERVICE_URL] ?: ""
        }

    suspend fun setUserLoginState(state: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_LOGIN_STATE] = state
        }
    }

    suspend fun setUserServiceUrl(serviceUrl: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_SERVICE_URL] = serviceUrl
        }
    }

    suspend fun removeUserPreferences() {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_LOGIN_STATE] = false
            preferences[PreferenceKeys.USER_SERVICE_URL] = ""
        }
    }
}