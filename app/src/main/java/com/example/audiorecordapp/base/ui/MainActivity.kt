package com.example.audiorecordapp.base.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.audiorecordapp.R
import com.example.audiorecordapp.base.adapters.AudioRecordAdapter
import com.example.audiorecordapp.base.models.local.AudioRecordObject
import com.example.audiorecordapp.base.utils.Animator
import com.example.audiorecordapp.base.utils.ConstantsUtils.REQ_CODE_READ_EXTERNAL_STORAGE_DOWNLOAD
import com.example.audiorecordapp.base.utils.ConstantsUtils.REQ_CODE_RECORD_AUDIO
import com.example.audiorecordapp.base.utils.ConstantsUtils.REQ_CODE_REC_AUDIO_AND_WRITE_EXTERNAL
import com.example.audiorecordapp.base.utils.ConstantsUtils.TOUCH_TOLERANCE
import com.example.audiorecordapp.base.utils.ConstantsUtils.bitRate
import com.example.audiorecordapp.base.utils.ConstantsUtils.sampleRate
import com.example.audiorecordapp.base.utils.ViewVisibility
import com.example.audiorecordapp.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity(), View.OnTouchListener {
    private lateinit var binding: ActivityMainBinding

    lateinit var audioRecordAdapter: AudioRecordAdapter
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    //For adding the audio in a File
    private lateinit var fileNewName: File
    private lateinit var fileNameMedia: String
    private lateinit var dirPathMain: String

    //To Handle pause and resume
    private var isRecordingMedia = false
    private var isPlayingMedia = false
    private var isPaused = false

    //To check the position of the audio if paused
    private var pausePos = 0f

    private var listOfRecords: ArrayList<AudioRecordObject> = arrayListOf()

    private var permissionIfStorageAccepted = false
    private var permissionIfAudioRecordAccepted = false

    private var isCanceled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        onClickListener()
        permissionViews()
//        setUpRecycler()
    }

    //TODO handle a folder where files are created and we can add files upon recording and finishing record
    private fun initMedia() {
        dirPathMain = "${externalCacheDir?.absolutePath}/"
        fileNameMedia = "New Record" + ".m4a"
        val fileMedia = File("$dirPathMain$fileNameMedia")
        if (fileMedia.exists()) {
            try {
                fileMedia.createNewFile()
            } catch (e: IOException) {
                Log.d("MainActivity", "could not create file $e")
                e.printStackTrace()
            }
        }
    }

    private fun permissionViews() {
        with(binding) {
            if (checkStoragePermission() && checkRecordPermission()) {
                ViewVisibility.viewShow(btRecord)
                ViewVisibility.viewShow(tvTimer)
                ViewVisibility.viewHide(tvAcceptPermission)
                ViewVisibility.viewHide(btPermission)
            } else {
                binding.btRecord.isEnabled = false
                ViewVisibility.viewHide(btRecord)
                ViewVisibility.viewHide(tvTimer)
                ViewVisibility.viewShow(tvAcceptPermission)
                ViewVisibility.viewShow(btPermission)
            }
        }
    }

    private fun onClickListener() {
        with(binding) {
            btPermission.setOnClickListener {
                checkRecordPermission()
                checkStoragePermission()
                permissionViews()
            }
            btRecord.setOnClickListener {
            }
            btCancel.setOnClickListener {
                Toast.makeText(this@MainActivity, "You Cancelled", Toast.LENGTH_SHORT).show()
            }
            btRecord.setOnTouchListener(this@MainActivity)
        }

    }

    private fun setUpRecycler(audioRecordObjectList: ArrayList<AudioRecordObject>) {
        audioRecordAdapter = AudioRecordAdapter { adapter ->
            audioRecordAdapter.getAudioRecord()[adapter].isPlaying =
                !audioRecordAdapter.getAudioRecord()[adapter].isPlaying
            if (audioRecordAdapter.getAudioRecord()[adapter].isPlaying && !isPlayingMedia && !isPaused)
                startPlaying(audioRecordAdapter.getAudioRecord()[adapter].audioPath)
            else if (isPlayingMedia && !audioRecordAdapter.getAudioRecord()[adapter].isPlaying && !isPaused)
                pausePlaying()
            else if (!isPlayingMedia && audioRecordAdapter.getAudioRecord()[adapter].isPlaying && isPaused)
                resumePlaying()
            else
                stopPlaying()
            audioRecordAdapter.notifyItemChanged(adapter)
        }
        binding.rvAudioList.adapter = audioRecordAdapter
        audioRecordAdapter.setAudioList(audioRecordObjectList)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_REC_AUDIO_AND_WRITE_EXTERNAL && grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
            permissionIfStorageAccepted = true
            binding.btRecord.isEnabled = true
        } else if (requestCode == REQ_CODE_RECORD_AUDIO && grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED)
            permissionIfAudioRecordAccepted = true
        binding.btRecord.isEnabled = true
    }

    private fun startRecording(id: Int) {
        if (id == binding.btRecord.id) {
            initMedia()
            if (mediaRecorder == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    mediaRecorder = MediaRecorder(baseContext)
                    mediaRecorder?.apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setAudioSamplingRate(sampleRate)
                        setAudioEncodingBitRate(bitRate)
                        setAudioChannels(2)
                        setMaxDuration(30000)
                        setOutputFile(dirPathMain + fileNameMedia)
                    }
                } else {
                    mediaRecorder = MediaRecorder()
                    mediaRecorder?.apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setAudioSamplingRate(sampleRate)
                        setAudioEncodingBitRate(bitRate)
                        setAudioChannels(2)
                        setMaxDuration(30000)
                        setOutputFile(dirPathMain + fileNameMedia)
                    }
                }
                try {
                    mediaRecorder?.prepare()
                } catch (e: IOException) {
                    Toast.makeText(
                        this,
                        "IOException while trying to prepare MediaRecorder",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("MainActivityThis", "could not prepare MediaRecorder $e")
                    return
                } catch (e: IllegalStateException) {

                    Toast.makeText(
                        this,
                        "IllegalStateException while trying to prepare MediaRecorder",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("MainActivityThis", "could not prepare MediaRecorder $e")
                    return
                }
                mediaRecorder?.start()
                Log.d("MainActivityThis", "recording started with MediaRecorder")
            }
        }
    }

    private fun stopRecording(btRecord: Int?, btCancel: Int?) {
        if (btRecord == binding.btRecord.id || btCancel == binding.btCancel.id) {
            if (mediaRecorder != null) {
                mediaRecorder?.stop()
                mediaRecorder?.reset()
                mediaRecorder?.release()
                Log.d("MainActivityThis", "recording Stopped")
                mediaRecorder = null
            }
        }
    }


    private fun startPlaying(audioFile: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setOnCompletionListener { mp: MediaPlayer? ->
                stopPlaying()
            }
            try {
                mediaPlayer?.apply {
                    setDataSource(audioFile)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    prepare()
                    start()
                }
                isPlayingMedia = true
                Log.d("MainActivityThis", "playback started with MediaPlayer")
            } catch (e: IOException) {
                Toast.makeText(
                    this,
                    "Couldn't prepare MediaPlayer, IOException",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(
                    "MainActivityThis",
                    "error reading from file while preparing MediaPlayer$e"
                )
            } catch (e: IllegalArgumentException) {
                Toast.makeText(
                    this,
                    "Couldn't prepare MediaPlayer, IllegalArgumentException",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("MainActivityThis", "illegal argument given $e")
            }
        }
    }

    private fun stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
            isPlayingMedia = false
            isPaused = false
        }
    }

    private fun pausePlaying() {
        if (mediaPlayer != null) {
            mediaPlayer?.pause()
            pausePos = mediaPlayer?.currentPosition.let { it?.toFloat() ?: 0f }
            isPlayingMedia = false
            isPaused = true
        }
    }


    private fun cancelRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder?.stop()
                mediaRecorder?.reset()
                val file = File("$dirPathMain$fileNameMedia")
                file.delete()
                Log.d("MainActivityThis", "recording Deleted ${file.absolutePath}")
                mediaRecorder = null
            } catch (e: IllegalArgumentException) {
                Toast.makeText(
                    this,
                    "Couldn't prepare MediaPlayer, IllegalArgumentException",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("MainActivityThis", "illegal argument given $e")
            }
        }
    }


    private fun resumePlaying() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.start()
                mediaPlayer?.seekTo(pausePos.toInt())
                isPlayingMedia = true
                isPaused = false
                mediaPlayer?.setOnCompletionListener { mp: MediaPlayer? ->
                    stopPlaying()
                }
            } catch (e: IllegalStateException) {
                Log.e("MainActivityThis", "failed $e")
            }
            Log.d("MainActivityThis", "recording Resumed")

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        when (motionEvent?.action) {
            MotionEvent.ACTION_DOWN -> {
                onRecord()
                if (!isRecordingMedia) {
                    try {
                        startRecording(binding.btRecord.id)
                    } catch (e: Exception) {
                        Log.e("MainActivityThis", "onManyClicks $e")
                        cancelRecording()
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                onReleaseCancel()
                if (!isCanceled) {
                    try {
                        stopRecording(binding.btRecord.id, null)
                        showSaveDialog(fileNameMedia)
                        isRecordingMedia = false
                    } catch (e: Exception) {
                        Log.e("MainActivityThis", "onManyClicks $e")
                        mediaRecorder = null
                    }
                } else {
                    isCanceled = false
                }
                return true
            }
            //TODO handle cancel button
            MotionEvent.ACTION_MOVE -> {
                if ((motionEvent.x - getStatusBarHeight()) - binding.root.top < TOUCH_TOLERANCE) {
                    onReleaseCancel()
                    cancelRecording()
                    Toast.makeText(baseContext, "You Cancelled", Toast.LENGTH_SHORT).show()
                    isCanceled = true
                    return true
                }
                return true
            }
        }
        return true
    }

    private fun showSaveDialog(
        fileName: String,
    ) {
        var id = 0
        if (!isCanceled) {
            val filePath = dirPathMain + fileNameMedia
            DialogSaveAudio(fileName, {
                val newFile = File("$dirPathMain$it.m4a")
                fileNewName = File(filePath)
                fileNewName.renameTo(newFile)
                ++id
                val newAudio = AudioRecordObject(
                    id,
                    it,
                    "$dirPathMain$it.m4a",
                    "00:25",
                    false
                )
                Log.d("MainActivityThis", "recording Saved ${newFile.absolutePath}")
                listOfRecords.add(newAudio)
                setUpRecycler(listOfRecords)
            }
            ) {
                val fileDelete = File("$dirPathMain$fileNameMedia")
                fileDelete.delete()
                Log.d("MainActivityThis", "recording Deleted ${fileDelete.absolutePath}")
            }.show(supportFragmentManager, "")
        }
    }


    private fun onRecord() {
        binding.apply {
            Animator.scaleUp(btRecord)
            Animator.scaleUp(progressBar)
            ViewVisibility.viewShow(btCancel)
            ViewVisibility.viewShow(animationView)
        }
    }

    private fun onReleaseCancel() {
        binding.apply {
            Animator.scaleDown(btRecord)
            Animator.scaleDown(progressBar)
            ViewVisibility.viewHide(btCancel)
            ViewVisibility.viewHide(animationView)
        }
    }

    private fun checkRecordPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQ_CODE_RECORD_AUDIO
                )
                return false
            }
        }
        return true
    }

    private fun checkStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    REQ_CODE_READ_EXTERNAL_STORAGE_DOWNLOAD
                )
                return false
            }
        }
        return true
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId =
            resources.getIdentifier("status_bar_height", "dimen", this.packageName)
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        Log.d("height", result.toString())
        return result
    }

}