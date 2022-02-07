package com.example.audiorecordapp.base.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.audiorecordapp.R
import com.example.audiorecordapp.base.models.local.AudioRecordObject
import com.example.audiorecordapp.databinding.ItemPlayAudioBinding

class AudioRecordAdapter(
    private val onPlayPausedClicked: (Int) -> Unit
) : RecyclerView.Adapter<AudioRecordAdapter.AudioListViewHolder>() {
    private var recordList: List<AudioRecordObject> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setAudioList(audioList: List<AudioRecordObject>) {
        this.recordList = audioList
        notifyDataSetChanged()
    }

    fun getAudioRecord() = recordList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayAudioBinding.inflate(inflater, parent, false)
        return AudioListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioListViewHolder, position: Int) {
        val audioListObject = recordList[position]
        holder.bind(audioListObject)
        holder.binding.btPlay.setOnClickListener {
            onPlayPausedClicked(position)
        }
    }

    override fun getItemCount(): Int = recordList.size

    class AudioListViewHolder(val binding: ItemPlayAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(audioListObject: AudioRecordObject) {
            with(binding) {
                val audioName = audioListObject.audioName.split(".m4a")[0]
                tvRecordName.text = audioName
                tvTime.text = audioListObject.audioDuration
                if (audioListObject.isPlaying) {
                    btPlay.setIconResource(R.drawable.ic_baseline_pause_24)
                } else if(!audioListObject.isPlaying){
                    btPlay.setIconResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
        }
    }
}