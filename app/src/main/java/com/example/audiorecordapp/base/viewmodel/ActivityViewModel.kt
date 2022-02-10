package com.example.audiorecordapp.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiorecordapp.base.models.local.models.AudioRecordList
import com.example.audiorecordapp.base.models.local.models.AudioRecordTimeObject
import com.example.audiorecordapp.base.models.local.prefstore.PrefsStoreImpl
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(private val prefsStoreImpl: PrefsStoreImpl) :
    ViewModel() {
    var audioTime: MutableLiveData<String> = MutableLiveData("")

    var audioRecordTimeObjectList: ArrayList<AudioRecordTimeObject> = arrayListOf()
    private var audioRecordListLiveData: MutableLiveData<AudioRecordList> =
        MutableLiveData(AudioRecordList(arrayListOf()))

    fun saveAudioTime(audioRecordTimeObject: AudioRecordTimeObject) {
        viewModelScope.launch(Dispatchers.IO) {
            audioRecordTimeObjectList.add(audioRecordTimeObject)
            val serializedResult = Gson().toJson(
                AudioRecordList(
                    audioRecordTimeObjectList
                )
            )
            prefsStoreImpl.setAudioTime(
                serializedResult
            )
        }
    }

    fun getAudioTime() {
        viewModelScope.launch(Dispatchers.IO) {
            prefsStoreImpl.getAudioTime().collect {
                val audioRecordList = Gson().fromJson(it, AudioRecordList::class.java)
                audioRecordListLiveData.postValue(audioRecordList)
            }
        }
    }
}