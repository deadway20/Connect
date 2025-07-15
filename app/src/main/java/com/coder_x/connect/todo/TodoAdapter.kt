package com.coder_x.connect.todo


import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.coder_x.connect.R
import com.coder_x.connect.database.NoteViewModel
import com.coder_x.connect.databinding.ItemTextTodoBinding
import com.coder_x.connect.databinding.ItemVoiceTodoBinding
import com.masoudss.lib.SeekBarOnProgressChanged
import java.io.File
import kotlin.random.Random

class TodoAdapter(
    private val todoList: MutableList<TodoData>,
    val context: Context,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onEdit: ((Int) -> Unit)? = null
    var onDelete: ((Int) -> Unit)? = null

    private var openedPosition: Int = -1
    private var isButtonClicked = false
    private val noteViewModel = NoteViewModel(context.applicationContext as Application)

    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition = -1
    private var progressHandler: Handler? = null
    private var progressRunnable: Runnable? = null
    private var timerRunnable: Runnable? = null
    private var timerHandler: Handler? = null

    fun closeOpenedItem() {
        if (openedPosition != -1) {
            val oldPosition = openedPosition
            openedPosition = -1
            notifyItemChanged(oldPosition)
        }
    }

    private fun isItemOpen(position: Int) = openedPosition == position

    fun setOpenedItem(position: Int) {
        if (openedPosition != position) {
            val oldPosition = openedPosition
            openedPosition = position
            if (oldPosition != -1) notifyItemChanged(oldPosition)
            if (position != -1) notifyItemChanged(position)
        }
    }

    fun updateList(newList: List<TodoData>) {
        todoList.clear()
        todoList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position == currentPlayingPosition) stopAudio()
        if (position >= 0 && position < todoList.size) {
            todoList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, todoList.size)
            if (openedPosition == position) openedPosition = -1
            else if (openedPosition > position) openedPosition--
        }
    }

    override fun getItemViewType(position: Int) = when (todoList[position].type) {
        TodoType.TEXT -> 0
        TodoType.VOICE -> 1
    }

    fun getItemAt(position: Int) = todoList[position]

    override fun getItemCount() = todoList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        0 -> TextTodoViewHolder(ItemTextTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        1 -> VoiceTodoViewHolder(ItemVoiceTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else -> throw IllegalArgumentException("Wrong Type")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = todoList[position]
        val density = holder.itemView.resources.displayMetrics.density
        val isOpen = isItemOpen(position)

        noteViewModel.getOrUpdateColor(item.id) { getRandomBrightColor() }
            .observe(context as androidx.lifecycle.LifecycleOwner) { color ->
                item.color = color
                when (holder) {
                    is TextTodoViewHolder -> bindTextTodo(holder, item, color, density, isOpen, position)
                    is VoiceTodoViewHolder -> bindVoiceTodo(holder, item, color, density, isOpen, position)
                }
            }

        when (holder) {
            is TextTodoViewHolder -> bindTextTodo(holder, item, item.color, density, isOpen, position)
            is VoiceTodoViewHolder -> bindVoiceTodo(holder, item, item.color, density, isOpen, position)
        }
    }

    private fun bindTextTodo(holder: TextTodoViewHolder, item: TodoData, color: Int, density: Float, isOpen: Boolean, position: Int) {
        holder.binding.todoView.setBackgroundColor(color)
        holder.binding.todoTitle.text = item.todoTitle
        holder.binding.todoTime.text = item.todoTime
        holder.foreground.translationX = if (isOpen) -150f * density else 0f
        holder.editBtn.setOnClickListener(null)
        holder.deleteBtn.setOnClickListener(null)

        // Set initial star icon based on isFavorite status
        if (item.isFavorite) {
            holder.binding.isFavoriteBtn.setImageResource(R.drawable.ic_star_filled)
        } else {
            holder.binding.isFavoriteBtn.setImageResource(R.drawable.ic_star_outline)
        }

        holder.binding.isFavoriteBtn.setOnClickListener {
            item.isFavorite = !item.isFavorite // Toggle the favorite status
            noteViewModel.setFavoriteTask(item.id, item.isFavorite)
            holder.binding.isFavoriteBtn.setImageResource(if (item.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)
        }

        if (isOpen) {
            holder.editBtn.setOnClickListener {
                if (!isButtonClicked) {
                    isButtonClicked = true
                    onEdit?.invoke(position)
                    closeOpenedItem()
                    holder.editBtn.postDelayed({ isButtonClicked = false }, 500)
                }
            }
            holder.deleteBtn.setOnClickListener {
                if (!isButtonClicked) {
                    isButtonClicked = true
                    onDelete?.invoke(position)
                    closeOpenedItem()
                    holder.deleteBtn.postDelayed({ isButtonClicked = false }, 500)
                }
            }
        }

        applyCompletionStyle(
            holder.binding.todoTitle,
            holder.binding.checkbox,
            holder.foreground,
            item.isCompleted,
            context
        )
        holder.binding.checkbox.setOnClickListener {
            item.isCompleted = !item.isCompleted
            noteViewModel.setTaskCompleted(item.id)
            notifyItemChanged(position)
        }
    }

    private fun startAudio(holder: VoiceTodoViewHolder, audioPath: String?, position: Int) {
        if (audioPath == null) return

        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioPath)
            prepare()
            start()
            setOnCompletionListener {
                stopAudio()
                notifyItemChanged(position) // يحدث الزر ويعيد الشكل إلى زر التشغيل
            }
        }
        currentPlayingPosition = position

        holder.binding.playPauseBtn.setImageResource(R.drawable.voice_pause_ico)

        val file = File(audioPath)
        if (file.exists()) {
            todoList[position].isWaveformProcessed = true
            holder.binding.waveformSeekBar.setSampleFrom(file)
        }
        startProgressUpdate(holder)
    }


    private fun bindVoiceTodo(
        holder: VoiceTodoViewHolder,
        item: TodoData,
        color: Int,
        density: Float,
        isOpen: Boolean,
        position: Int
    ) {

        val duration = item.totalDuration ?: 0L
        holder.binding.todoView.setBackgroundColor(color)
        holder.binding.todoTitle.text = item.todoTitle
        holder.binding.voiceTime.text = item.todoTime
        if (position != currentPlayingPosition) {
            holder.binding.audioTimer.text = formatMillisToTime(duration)
        }
        if (!item.isWaveformProcessed && item.audioPath != null) {
            holder.binding.waveformSeekBar.setSampleFrom(item.audioPath)
            item.isWaveformProcessed = true
        }


        holder.foreground.translationX = if (isOpen) -150f * density else 0f
        holder.binding.waveformSeekBar.progress = if (position == currentPlayingPosition) {
            (mediaPlayer?.currentPosition?.toFloat() ?: 0f) / (mediaPlayer?.duration?.toFloat() ?: 1f)
        } else 0f

        holder.binding.playPauseBtn.setOnClickListener {
            if (position == currentPlayingPosition) {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    holder.binding.playPauseBtn.setImageResource(R.drawable.play_media_ico) // Change to play icon
                } else {
                    mediaPlayer?.start()
                    startTimer(holder, duration)
                    holder.binding.playPauseBtn.setImageResource(R.drawable.voice_pause_ico) // Change to pause icon
                }
            } else {
                stopAudio()
                startAudio(holder, item.audioPath, position)
                startTimer(holder, duration)
            }
        }

        holder.binding.waveformSeekBar.onProgressChanged = object : SeekBarOnProgressChanged {
            override fun onProgressChanged(
                waveformSeekBar: com.masoudss.lib.WaveformSeekBar,
                progress: Float,
                fromUser: Boolean,
            ) {
                if (fromUser && position == currentPlayingPosition && mediaPlayer != null) {
                    val seekPosition = ((mediaPlayer!!.duration * progress) / 100).toInt()
                    mediaPlayer!!.seekTo(seekPosition)
                }
            }
        }


        // Set initial star icon based on isFavorite status
        if (item.isFavorite) {
            holder.binding.isFavoriteBtn.setImageResource(R.drawable.ic_star_filled)
        } else {
            holder.binding.isFavoriteBtn.setImageResource(R.drawable.ic_star_outline)
        }

        holder.binding.isFavoriteBtn.setOnClickListener {
            item.isFavorite = !item.isFavorite // Toggle the favorite status
            noteViewModel.setFavoriteTask(item.id, item.isFavorite)
            holder.binding.isFavoriteBtn.setImageResource(if (item.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)
        }

        applyCompletionStyle(
            holder.binding.todoTitle,
            holder.binding.checkbox,
            holder.foreground,
            item.isCompleted,
            context
        )
        holder.binding.checkbox.setOnClickListener {
            item.isCompleted = !item.isCompleted
            noteViewModel.setTaskCompleted(item.id)
            notifyItemChanged(position)
        }




        if (isOpen) {
            holder.editBtn.setOnClickListener {
                if (!isButtonClicked) {
                    isButtonClicked = true
                    onEdit?.invoke(position)
                    closeOpenedItem()
                    holder.editBtn.postDelayed({ isButtonClicked = false }, 500)
                }
            }
            holder.deleteBtn.setOnClickListener {
                if (!isButtonClicked) {
                    isButtonClicked = true
                    onDelete?.invoke(position)
                    closeOpenedItem()
                    holder.deleteBtn.postDelayed({ isButtonClicked = false }, 500)
                }
            }
        }
    }


    fun stopAudio() {
        mediaPlayer?.stop() // Use stop() instead of release() to allow resuming
        mediaPlayer?.reset() // Reset to idle state
        // mediaPlayer?.release() // Release only when the adapter is destroyed or audio is no longer needed
        // mediaPlayer = null

        if (progressRunnable != null) {
            progressHandler?.removeCallbacks(progressRunnable!!)
        }
        progressHandler = null
        progressRunnable = null

        if (timerRunnable != null) {
            timerHandler?.removeCallbacks(timerRunnable!!)
        }
        timerHandler = null
        timerRunnable = null

        val oldPosition = currentPlayingPosition
        currentPlayingPosition = -1

        if (oldPosition != -1) {
            // Optionally reset waveform or keep it, depending on desired behavior
            // todoList[oldPosition].isWaveformProcessed = true
            notifyItemChanged(oldPosition) // يرجّع زر التشغيل
        }

    }

    private fun startProgressUpdate(holder: VoiceTodoViewHolder) {
        progressHandler = Handler(Looper.getMainLooper())
        progressRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer != null && currentPlayingPosition != -1) {
                    val duration = mediaPlayer!!.duration
                    val current = mediaPlayer!!.currentPosition
                    val progress = (current * 100f) / duration
                    holder.binding.waveformSeekBar.progress = progress
                    progressHandler?.postDelayed(this, 300)
                }
            }
        }
        progressHandler?.post(progressRunnable!!)
    }

    private fun startTimer(holder: VoiceTodoViewHolder, duration: Long) {
        timerHandler = Handler(Looper.getMainLooper())
        timerRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    val elapsedTime = it.currentPosition.toLong()
                    val remainingTime = duration - elapsedTime
                    holder.binding.audioTimer.text = formatMillisToTime(remainingTime.coerceAtLeast(0))

                    if (it.isPlaying) {
                        timerHandler?.postDelayed(this, 1000)
                    }
                }
            }
        }
        timerHandler?.post(timerRunnable!!)
    }

    private fun applyCompletionStyle(
        title: TextView,
        checkbox: CheckBox,
        foreground: View,
        isCompleted: Boolean,
        context: Context,
    ) {
        if (isCompleted) {
            title.paintFlags = title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            title.setTextColor(Color.GRAY)
            checkbox.isChecked = true
            foreground.animate().alpha(0.6f).setDuration(250).start()
        } else {
            title.paintFlags = title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            title.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            checkbox.isChecked = false
            foreground.animate().alpha(1f).setDuration(250).start()
        }
    }


    private fun formatMillisToTime(millis: Long?): String {
        if (millis == null) return "00:00"
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getRandomBrightColor(): Int {
        val red = Random.nextInt(128, 256)
        val green = Random.nextInt(128, 256)
        val blue = Random.nextInt(128, 256)
        return Color.rgb(red, green, blue)
    }


    inner class TextTodoViewHolder(val binding: ItemTextTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val foreground: View = binding.root.findViewById(R.id.foreground_layout)
        val editBtn: View = binding.root.findViewById(R.id.edit_button)
        val deleteBtn: View = binding.root.findViewById(R.id.delete_button)
    }

    inner class VoiceTodoViewHolder(val binding: ItemVoiceTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val foreground: View = binding.root.findViewById(R.id.foreground_layout)
        val editBtn: View = binding.root.findViewById(R.id.edit_button)
        val deleteBtn: View = binding.root.findViewById(R.id.delete_button)
    }
}
