package com.example.audiorecordapp.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiorecordapp.base.models.local.prefstore.PrefsStoreImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(private val prefsStoreImpl: PrefsStoreImpl) :
    ViewModel() {
    var audioTime: MutableLiveData<String> = MutableLiveData("")


    fun saveAudioTime(audioTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            prefsStoreImpl.setAudioTime(
                audioTime
            )
        }
    }

    fun getAudioTime() {
        viewModelScope.launch(Dispatchers.IO) {
            prefsStoreImpl.getAudioTime().collect {
                audioTime.postValue(it)
            }
        }
    }
}