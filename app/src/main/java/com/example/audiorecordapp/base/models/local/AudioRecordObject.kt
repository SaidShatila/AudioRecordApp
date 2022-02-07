package com.example.audiorecordapp.base.models.local

data class AudioRecordObject(
    var audioId: Int?,
    var audioName: String,
    var audioPath: String,
    var audioDuration: String,
    var isPlaying: Boolean = false,
    var isStopped: Boolean = false
)