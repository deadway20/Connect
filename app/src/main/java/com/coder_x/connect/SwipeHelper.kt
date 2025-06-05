package com.coder_x.connect

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class SwipeHelper(
    private val adapter: TodoAdapter
) : ItemTouchHelper.Callback() {

    private var currentDx = 0f
    private var swipedPosition: Int = -1
    private var currentForeground: View? = null
    private val maxSwipeDistance = 100f * Resources.getSystem().displayMetrics.density

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // لا نستخدم onSwiped لأننا نريد التحكم الكامل في onChildDraw
    }

    override fun onChildDraw(
        c: android.graphics.Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) return

        val foreground = when (viewHolder) {
            is TodoAdapter.TextTodoViewHolder -> viewHolder.foreground
            is TodoAdapter.VoiceTodoViewHolder -> viewHolder.foreground
            else -> return
        }

        val position = viewHolder.bindingAdapterPosition
        if (position == RecyclerView.NO_POSITION) return

        // إذا كان هناك عنصر مفتوح غير العنصر الحالي، أغلقه
        if (swipedPosition != position && swipedPosition != -1) {
            adapter.closeOpenedItem()
        }

        // تحديث الموقع الحالي فقط إذا كان المستخدم يقوم بالسحب
        if (isCurrentlyActive) {
            swipedPosition = position
            currentDx = dX.coerceIn(-maxSwipeDistance, 0f)
            foreground.translationX = currentDx
            currentForeground = foreground
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        val foreground = when (viewHolder) {
            is TodoAdapter.TextTodoViewHolder -> viewHolder.foreground
            is TodoAdapter.VoiceTodoViewHolder -> viewHolder.foreground
            else -> return
        }

        val position = viewHolder.bindingAdapterPosition
        if (position == RecyclerView.NO_POSITION) return

        // التحقق من أن هذا هو العنصر الذي كان يتم سحبه
        if (swipedPosition == position) {
            if (abs(currentDx) < maxSwipeDistance * 0.5f) {
                // أرجع العنصر لمكانه الأصلي
                foreground.animate()
                    .translationX(0f)
                    .setDuration(200)
                    .withEndAction {
                        adapter.setOpenedItem(-1)
                    }
                    .start()
                swipedPosition = -1
            } else {
                // ثبت العنصر في الموقع المفتوح
                foreground.animate()
                    .translationX(-maxSwipeDistance)
                    .setDuration(200)
                    .withEndAction {
                        adapter.setOpenedItem(position)
                    }
                    .start()
            }
        }
    }

    // دالة لإغلاق العنصر المفتوح برمجياً
    fun closeOpenedItem() {
        if (swipedPosition != -1) {
            currentForeground?.animate()
                ?.translationX(0f)
                ?.setDuration(200)
                ?.withEndAction {
                    adapter.setOpenedItem(-1)
                    swipedPosition = -1
                    currentForeground = null
                }
                ?.start()
        }
    }

    override fun isItemViewSwipeEnabled(): Boolean = true
    override fun isLongPressDragEnabled(): Boolean = false
}