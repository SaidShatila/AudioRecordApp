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

    var audioRecordTimeObjectList: ArrayList<AudioRecordTimeObject> = arrayListOf()
     var audioRecordListLiveData: MutableLiveData<AudioRecordList> =
        MutableLiveData(AudioRecordList(arrayListOf()))


    fun saveAudioTime(audioRecordTimeObject: AudioRecordTimeObject) {
        viewModelScope.launch(
            Dispatchers.Main
        ) {
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

    fun getAudioTime(onLaunch: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            prefsStoreImpl.getAudioTime().collect { it ->
                val audioRecordList = Gson().fromJson(it, AudioRecordList::class.java)
                if (audioRecordListLiveData.value != null && onLaunch) {
                    val tempList: ArrayList<AudioRecordTimeObject> = arrayListOf()
                    audioRecordListLiveData.value
                        ?.audioRecordTimeObject?.forEach { content ->
                            tempList.add(content)
                        }
                    audioRecordList.audioRecordTimeObject.forEach { content ->
                        tempList.add(content)
                    }
                    tempList.sortBy {
                        it.audioTime.length
                    }
                    val audioRecordListNew = AudioRecordList(tempList)
                    audioRecordListLiveData.postValue(audioRecordListNew)
                } else audioRecordListLiveData.postValue(audioRecordList)
            }
        }
    }


    fun setTAudioTimes(){
        viewModelScope.launch(Dispatchers.Main) {
            val serializedObject =  Gson().toJson(
                audioRecordListLiveData.value?.audioRecordTimeObject?.let {
                    AudioRecordList(
                        it
                    )
                }
            )
            prefsStoreImpl.setAudioTime(serializedObject)
        }
    }
}