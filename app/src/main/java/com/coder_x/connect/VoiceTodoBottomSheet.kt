
package com.coder_x.connect

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.coder_x.connect.databinding.BottomsheetVoiceTodoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.coder_x.connect.database.NoteEntity

class VoiceTodoBottomSheet : BottomSheetDialogFragment() {

    // [START Binding Section]
    private var _binding: BottomsheetVoiceTodoBinding? = null
    private val binding get() = _binding!!
    // [END Binding Section]

    // [START Callbacks Section]
    var onVoiceTodoUpdated: ((todo: NoteEntity) -> Unit)? = null
    var onVoiceTodoAdded: ((title: String, audioPath: String, duration: String) -> Unit)? = null
    // [END Callbacks Section]

    // [START Audio Recording Section]
    private var mediaRecorder: MediaRecorder? = null
    private var outputFilePath: String = ""
    private var isRecording = false
    private var startTime: Long = 0L
    private var currentTodoId: Long? = null
    // [END Audio Recording Section]

    // [START View Creation Section]
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetVoiceTodoBinding.inflate(inflater, container, false)
        return binding.root
    }
    // [END View Creation Section]

    // [START View Interaction Section]
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recordButton.setOnClickListener {
            if (!isRecording) {
                if (checkAudioPermission()) {
                    startRecording()
                    binding.recordingTimer.visibility = View.VISIBLE
                    binding.recordingTimer.text = "00:00"
                    startTime = System.currentTimeMillis()
                    binding.recordButton.setImageResource(R.drawable.voice_pause_ico)
                    isRecording = true
                }
            } else {
                stopRecording()
                val duration = formatDuration(System.currentTimeMillis() - startTime)
                binding.recordingTimer.text = duration
                binding.recordButton.setImageResource(R.drawable.mic_recorder_ico)
                isRecording = false
            }
        }

        binding.cancelButton.setOnClickListener {
            if (isRecording) stopRecording()
            dismiss()
        }

        setupAddButton()
    }
    // [END View Interaction Section]

    // [START Audio Functions Section]
    private fun startRecording() {
        outputFilePath = "${requireContext().externalCacheDir?.absolutePath}/voice_todo_${System.currentTimeMillis()}.3gp"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFilePath)
            prepare()
            start()
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }
    // [END Audio Functions Section]

    // [START Helper Functions Section]
    private fun formatDuration(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun checkAudioPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 101)
            false
        } else {
            true
        }
    }
    // [END Helper Functions Section]

    // [START Edit Mode Setup Section]
    fun setTodoForEditing(todoId: Long, todo: NoteEntity) {
        currentTodoId = todoId
        if (_binding != null) {
            binding.voiceTitleInput.setText(todo.title)
            outputFilePath = todo.audioPath ?: ""
            binding.recordingTimer.text = formatDuration(todo.audioDuration ?: 0L)
        }
    }
    // [END Edit Mode Setup Section]

    // [START Add Button Logic Section]
    private fun setupAddButton() {
        binding.addButton.setOnClickListener {
            val title = binding.voiceTitleInput.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), R.string.title_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (outputFilePath.isEmpty() && currentTodoId == null) {
                Toast.makeText(requireContext(), R.string.record_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duration = if (isRecording) {
                formatDuration(System.currentTimeMillis() - startTime)
            } else {
                binding.recordingTimer.text.toString()
            }

            if (currentTodoId != null) {
                // Edit existing todo
                val updatedTodo = NoteEntity(
                    id = currentTodoId!!,
                    title = title,
                    audioPath = outputFilePath,
                    audioDuration = duration.toLongOrNull() ?: 0L,
                    timestamp = System.currentTimeMillis(),
                    isAudio = true,
                    isCompleted = false,
                    type = TodoType.VOICE.name


                )
                onVoiceTodoUpdated?.invoke(updatedTodo)
            } else {
                // Add new todo
                onVoiceTodoAdded?.invoke(title, outputFilePath, duration)
            }
            dismiss()
        }
    }
    // [END Add Button Logic Section]

    // [START Lifecycle Section]
    override fun getTheme(): Int = R.style.Theme_Connect_BottomSheet_Material3

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // [END Lifecycle Section]
}