package com.example.audiorecordapp.base.utils

import android.media.AudioFormat

object ConstantsUtils {
    const val REQ_CODE_REC_AUDIO_AND_WRITE_EXTERNAL = 101
    const val REQ_RECORD_AUDIO_PERMISSION =200
    const val REQ_CODE_RECORD_AUDIO = 303
    const val REQ_CODE_WRITE_EXTERNAL_STORAGE = 404
    const val REQ_CODE_READ_EXTERNAL_STORAGE_IMPORT = 405
    const val REQ_CODE_READ_EXTERNAL_STORAGE_PLAYBACK = 406
    const val REQ_CODE_READ_EXTERNAL_STORAGE_DOWNLOAD = 407
    const val REQ_CODE_IMPORT_AUDIO = 11
    const val TOUCH_TOLERANCE = 10f

    const val TAG = "MainActivity"

    //Audio Recorder
    const val sampleRate = 44100 // supported on all devices
    const val channelConfigIn = AudioFormat.CHANNEL_IN_STEREO
    const val channelConfigOut = AudioFormat.CHANNEL_OUT_STEREO
    const val audioFormat = AudioFormat.ENCODING_PCM_16BIT // supported on all devices
    const val bitRate = 192000 // supported on all devices

}