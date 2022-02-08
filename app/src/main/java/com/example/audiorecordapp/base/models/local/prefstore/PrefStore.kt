package com.example.audiorecordapp.base.models.local.prefstore

import kotlinx.coroutines.flow.Flow

  interface PrefStore {

    fun getAudioTime(): Flow<String>
    suspend fun setAudioTime(audioTimeList: String)
}