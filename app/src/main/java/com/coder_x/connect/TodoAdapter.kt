package com.coder_x.connect

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coder_x.connect.databinding.ItemTextTodoBinding
import com.coder_x.connect.databinding.ItemVoiceTodoBinding
import kotlin.random.Random

class TodoAdapter(
    private val todoList: MutableList<TodoData>, // تغيير إلى MutableList
    val context: Context,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onEdit: ((Int) -> Unit)? = null
    var onDelete: ((Int) -> Unit)? = null
    private var openedPosition: Int = -1
    private var isButtonClicked = false // متغير لتتبع النقر على الأزرار

    fun closeOpenedItem() {
        if (openedPosition != -1) {
            val oldPosition = openedPosition
            openedPosition = -1
            notifyItemChanged(oldPosition)
        }
    }

    private fun isItemOpen(position: Int): Boolean = openedPosition == position

    fun setOpenedItem(position: Int) {
        if (openedPosition != position) {
            val oldPosition = openedPosition
            openedPosition = position

            // أعد رسم العنصر القديم والجديد
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition)
            }
            if (position != -1) {
                notifyItemChanged(position)
            }
        }
    }

    // دالة لتحديث القائمة
    fun updateList(newList: List<TodoData>) {
        todoList.clear()
        todoList.addAll(newList)
        notifyDataSetChanged()
    }

    // دالة لحذف عنصر من القائمة
    fun removeItem(position: Int) {
        if (position >= 0 && position < todoList.size) {
            todoList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, todoList.size)

            // إذا كان العنصر المحذوف هو المفتوح، أعد تعيين openedPosition
            if (openedPosition == position) {
                openedPosition = -1
            } else if (openedPosition > position) {
                openedPosition--
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = ItemTextTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TextTodoViewHolder(binding)
            }
            1 -> {
                val binding = ItemVoiceTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                VoiceTodoViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Wrong Type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (todoList[position].type) {
            TodoType.TEXT -> 0
            TodoType.VOICE -> 1
        }
    }

    fun getItemAt(position: Int): TodoData {
        return todoList[position]
    }

    override fun getItemCount(): Int = todoList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = todoList[position]
        val color = getRandomBrightColor()
        val density = holder.itemView.resources.displayMetrics.density
        val isOpen = isItemOpen(position)

        when (holder) {
            is TextTodoViewHolder -> {
                bindTextTodo(holder, item, color, density, isOpen, position)
            }
            is VoiceTodoViewHolder -> {
                bindVoiceTodo(holder, item, color, density, isOpen, position)
            }
        }
    }

    private fun bindTextTodo(
        holder: TextTodoViewHolder,
        item: TodoData,
        color: Int,
        density: Float,
        isOpen: Boolean,
        position: Int
    ) {
        holder.binding.todoView.setBackgroundColor(color)
        holder.binding.foregroundLayout.strokeWidth = density.toInt()
        holder.binding.foregroundLayout.strokeColor = color
        holder.binding.todoTitle.text = item.todoTitle
        holder.binding.todoTime.text = item.todoTime

        // تعيين موقع العنصر
        holder.foreground.translationX = if (isOpen) -100f * density else 0f

        // إزالة المستمعين السابقين
        holder.editBtn.setOnClickListener(null)
        holder.deleteBtn.setOnClickListener(null)

        // إضافة المستمعين فقط إذا كان العنصر مفتوحاً
        if (isOpen) {
            holder.editBtn.setOnClickListener {
                if (!isButtonClicked) {
                    isButtonClicked = true
                    onEdit?.invoke(position)
                    closeOpenedItem()
                    // إعادة تعيين المتغير بعد تأخير قصير
                    holder.editBtn.postDelayed({ isButtonClicked = false }, 500)
                }
            }
            holder.deleteBtn.setOnClickListener {
                if (!isButtonClicked) {
                    isButtonClicked = true
                    onDelete?.invoke(position)
                    closeOpenedItem()
                    // إعادة تعيين المتغير بعد تأخير قصير
                    holder.deleteBtn.postDelayed({ isButtonClicked = false }, 500)
                }
            }
        }
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
        holder.binding.foregroundLayout.strokeWidth = density.toInt()
        holder.binding.foregroundLayout.strokeColor = color
        holder.binding.voiceTitle.text = item.todoTitle
        holder.binding.voiceTime.text = item.todoTime
        holder.binding.voiceDuration.text = formatMillisToTime(item.totalDuration)

        // تعيين موقع العنصر
        holder.foreground.translationX = if (isOpen) -100f * density else 0f

        // إزالة المستمعين السابقين
        holder.editBtn.setOnClickListener(null)
        holder.deleteBtn.setOnClickListener(null)

        // إضافة المستمعين فقط إذا كان العنصر مفتوحاً
        if (isOpen) {
            holder.editBtn.setOnClickListener {
                if (!isButtonClicked) {
                    isButtonClicked = true
                    onEdit?.invoke(position)
                    closeOpenedItem()
                    // إعادة تعيين المتغير بعد تأخير قصير
                    holder.editBtn.postDelayed({ isButtonClicked = false }, 500)
                }
            }
            holder.deleteBtn.setOnClickListener {
                if (!isButtonClicked) {
                    isButtonClicked = true
                    onDelete?.invoke(position)
                    closeOpenedItem()
                    // إعادة تعيين المتغير بعد تأخير قصير
                    holder.deleteBtn.postDelayed({ isButtonClicked = false }, 500)
                }
            }
        }
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