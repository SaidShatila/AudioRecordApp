package com.example.audiorecordapp.base.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.audiorecordapp.databinding.DialogSaveAudioBinding
import java.io.File

class DialogSaveAudio(
    private val filename: String,
    val onSave: (fileName: String) -> Unit?,
    val onCancel: (fileName: String) -> Unit
) : DialogFragment() {


    private lateinit var binding: DialogSaveAudioBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSaveAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filename = filename.split(".m4a")[0]
        var appSpecificExternalDir: File
        with(binding) {
            filenameInput.setText(filename)
            var updatedFileNameTop = filenameInput.text.toString()
            appSpecificExternalDir =
                File("${context?.externalCacheDir?.absolutePath}${File.separator}/")


            filenameInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(text: Editable?) {
//                    appSpecificExternalDir = File(context?.externalCacheDir, text.toString())
                    Log.v("path", appSpecificExternalDir.name.toString())
                    updatedFileNameTop = text.toString()
                    if (text.toString() == appSpecificExternalDir.name.toString()) {
                        Toast.makeText(
                            requireContext(),
                            "File Already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

            showKeyboard(filenameInput)
            okBtn.setOnClickListener {
                hideKeyboard(view)
                val updatedFilename = binding.filenameInput.text.toString()
                when {
                    updatedFilename == "New Record" -> {
                        Toast.makeText(
                            requireContext(),
                            "Please change the file name",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    checkIfFileExists(
                        appSpecificExternalDir,
                        updatedFileNameTop
                    ) -> {
                        Toast.makeText(
                            requireContext(),
                            "File Already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    updatedFilename != filename -> {
                        dismiss()
                        onSave(updatedFilename)
                    }
                }

            }

            cancelBtn.setOnClickListener {
                hideKeyboard(view)
                val updatedFilename = binding.filenameInput.text.toString()
                dismiss()
                onCancel(updatedFilename)
            }
        }

    }

    private fun showKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun checkIfFileExists(file: File, newFileName: String): Boolean {
        val listAllFiles = file.listFiles()
        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            if (listAllFiles.size == 1) {
                return false
            } else {
                for (currentFile in listAllFiles) {
                    if (currentFile.name.toString().replace(".m4a", "") == newFileName)
                        return true
                }
            }
        }
        return false
    }
}
