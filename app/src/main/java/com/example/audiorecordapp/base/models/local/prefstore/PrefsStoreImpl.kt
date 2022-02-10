package com.example.audiorecordapp.base.models.local.prefstore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val STORE_NAME = "audio_record_data_store"
val Context.dataStoreAudio: DataStore<Preferences> by preferencesDataStore(STORE_NAME)

@Singleton
class PrefsStoreImpl@Inject constructor(
    @ApplicationContext context: Context
) : PrefStore {


    private val dataStore: DataStore<Preferences> = context.dataStoreAudio


    private object PreferencesKeys {
        val AUDIO_TIME = stringPreferencesKey("audio_time")
    }

    override fun getAudioTime(): Flow<String> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { it[PreferencesKeys.AUDIO_TIME] ?: " " }

    override suspend fun setAudioTime(audioTimeList: String) {
        dataStore.edit {
            it[PreferencesKeys.AUDIO_TIME] = audioTimeList
        }
    }
}