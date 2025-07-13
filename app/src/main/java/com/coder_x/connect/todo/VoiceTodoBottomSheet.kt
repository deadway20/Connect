
package com.coder_x.connect.todo

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.coder_x.connect.R
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
    var onVoiceTodoAdded: ((title: String, audioPath: String, duration: Long) -> Unit)? = null
    // [END Callbacks Section]

    // [START Audio Recording Section]
    private var mediaRecorder: MediaRecorder? = null
    private var outputFilePath: String = ""
    private var isRecording = false
    private var startTime: Long = 0L
    private var currentTodoId: Long? = null
    private val handler = Handler()
    private lateinit var timerRunnable: Runnable
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
                    binding.recordButton.setImageResource(R.drawable.voice_pause_ico)
                    isRecording = true
                    binding.recordingTimer.visibility = View.VISIBLE
                    startTimer()
                }
            } else {
                stopRecording()
                stopTimer()
                // Duration is already updated by the timer
                binding.recordButton.setImageResource(R.drawable.mic_recorder_ico)
                isRecording = false
            }
        }

        binding.cancelButton.setOnClickListener {
            if (isRecording) stopRecording()
            stopTimer()
            dismiss()
        }
        todoForAdding()
    }

    private fun startRecording() {
        outputFilePath = "${requireContext().externalCacheDir?.absolutePath}/voice_todo_${System.currentTimeMillis()}.m4a"

        startTime = System.currentTimeMillis()

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
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

    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                if (isRecording) {
                    val millis = System.currentTimeMillis() - startTime
                    binding.recordingTimer.text = formatDuration(millis)
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
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
    fun todoForEditing(todoId: Long, todo: NoteEntity) {
        currentTodoId = todoId
        if (_binding != null) {
            binding.voiceTitleInput.setText(todo.title)
            outputFilePath = todo.audioPath ?: ""
            binding.recordingTimer.text = formatDuration(todo.audioDuration ?: 0L)
        }
    }
    // [END Edit Mode Setup Section]

    private fun todoForAdding() {
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

            val duration = System.currentTimeMillis() - startTime

            if (currentTodoId != null) {
                // Edit existing todo
                val updatedTodo = NoteEntity(
                    id = currentTodoId!!,
                    title = title,
                    audioPath = outputFilePath,
                    audioDuration = duration,
                    timestamp = if (isRecording) startTime else System.currentTimeMillis(),
                    isAudio = true,
                    isCompleted = false,
                    type = TodoType.VOICE.name
                )
                onVoiceTodoUpdated?.invoke(updatedTodo)
            } else {
                // Add new todo
                onVoiceTodoAdded?.invoke(title, outputFilePath, duration)
                Log.d("DURATION_ADDING","$duration")
            }

            if (isRecording) stopTimer()
            dismiss()
        }
    }


    override fun getTheme(): Int = R.style.Theme_Connect_BottomSheet_Material3

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (isRecording) stopTimer() // Ensure timer is stopped if sheet is dismissed while recording
    }
}