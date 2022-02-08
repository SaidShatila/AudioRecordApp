package com.example.audiorecordapp.base.models.local.models

data class AudioRecordObject(
    var audioId: Int?,
    var audioName: String,
    var audioPath: String,
    var audioDuration: String,
    var isPlaying: Boolean = false,
    var isStopped: Boolean = false
)