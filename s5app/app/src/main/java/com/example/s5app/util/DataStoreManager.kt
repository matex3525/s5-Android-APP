package com.example.s5app.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.s5app.model.AlbumTokens

import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(name = "storage")

@ViewModelScoped
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val albumTokensKey = stringPreferencesKey("album_tokens_key")

    val albumTokensFlow: Flow<List<AlbumTokens>> = context.dataStore.data
        .map { preferences ->
            preferences[albumTokensKey]?.let { json ->
                Json.decodeFromString<List<AlbumTokens>>(json)
            } ?: emptyList()
        }

    private suspend fun saveAlbumTokens(albumTokens: List<AlbumTokens>) {
        val json = Json.encodeToString(albumTokens)
        context.dataStore.edit { preferences ->
            preferences[albumTokensKey] = json
        }
    }

    suspend fun addOrUpdateAlbumToken(newToken: AlbumTokens): List<AlbumTokens> {
        val currentTokens = albumTokensFlow.first()
        val updatedTokens = currentTokens.toMutableList()

        val existingTokenIndex = updatedTokens.indexOfFirst {
            it.userToken == newToken.userToken
        }

        if (existingTokenIndex != -1) {
            val existingToken = updatedTokens[existingTokenIndex]
            if (existingToken.adminToken == null) {
                updatedTokens[existingTokenIndex] = newToken
            } else if (existingToken.adminToken == newToken.adminToken) {
                return currentTokens
            } else if (newToken.adminToken == null) {
                return currentTokens
            } else {
                updatedTokens.add(newToken)
            }
        } else {
            updatedTokens.add(newToken)
        }

        saveAlbumTokens(updatedTokens)
        return updatedTokens
    }
}