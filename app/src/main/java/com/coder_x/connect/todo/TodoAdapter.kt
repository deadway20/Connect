package com.coder_x.connect.todo


import android.app.Application
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coder_x.connect.R
import com.coder_x.connect.database.NoteViewModel
import com.coder_x.connect.databinding.ItemTextTodoBinding
import com.coder_x.connect.databinding.ItemVoiceTodoBinding
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

    private fun startAudio(holder: VoiceTodoViewHolder, audioPath: String?, position: Int) {
        if (audioPath == null) return

        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioPath)
            prepare()
            start()
            setOnCompletionListener { stopAudio() }
        }
//        currentPlayingPosition = position
//        notifyItemChanged(position)

        // الحل الجديد: إعادة تحميل waveform بعد notify
        val file = File(audioPath)
        if (file.exists()) {
            todoList[position].isWaveformProcessed = true // تأكيد المعالجة
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
        holder.binding.todoView.setBackgroundColor(color)
        holder.binding.voiceTitle.text = item.todoTitle
        holder.binding.voiceTime.text = item.todoTime
        holder.binding.voiceDuration.text = formatMillisToTime(item.totalDuration)
        holder.foreground.translationX = if (isOpen) -150f * density else 0f

        val audioPath = item.audioPath
        val file = File(audioPath ?: "")
        if (!file.exists()) {
            Log.e("AUDIO_CHECK", "الملف مش موجود: $audioPath")
        } else {
            Log.d("AUDIO_CHECK", "الملف موجود وجاهز للتشغيل: $audioPath")
        }

        if (!item.isWaveformProcessed && item.audioPath != null) {
            holder.binding.waveformSeekBar.setSampleFrom(item.audioPath)
            item.isWaveformProcessed = true
        }


        holder.binding.waveformSeekBar.progress = if (position == currentPlayingPosition) {
            (mediaPlayer?.currentPosition?.toFloat() ?: 0f) / (mediaPlayer?.duration?.toFloat() ?: 1f)
        } else 0f

        holder.binding.playPauseBtn.setImageResource(
            if (position == currentPlayingPosition) R.drawable.voice_pause_ico else R.drawable.play_media_ico
        )

        holder.binding.playPauseBtn.setOnClickListener {
            if (position == currentPlayingPosition) stopAudio()
            else {
                stopAudio()
                startAudio(holder, item.audioPath, position)
            }
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


    private fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null

        progressHandler?.removeCallbacks(progressRunnable!!)
        progressHandler = null
        progressRunnable = null

        val oldPosition = currentPlayingPosition
        currentPlayingPosition = -1

        if (oldPosition != -1) {
            todoList[oldPosition].isWaveformProcessed = false // Reset لو حابب تعيد المعالجة
            notifyItemChanged(oldPosition)
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

    inner class TextTodoViewHolder(val binding: ItemTextTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        val foreground: View = binding.root.findViewById(R.id.foreground_layout)
        val editBtn: View = binding.root.findViewById(R.id.edit_button)
        val deleteBtn: View = binding.root.findViewById(R.id.delete_button)
    }

    inner class VoiceTodoViewHolder(val binding: ItemVoiceTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        val foreground: View = binding.root.findViewById(R.id.foreground_layout)
        val editBtn: View = binding.root.findViewById(R.id.edit_button)
        val deleteBtn: View = binding.root.findViewById(R.id.delete_button)
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
}
