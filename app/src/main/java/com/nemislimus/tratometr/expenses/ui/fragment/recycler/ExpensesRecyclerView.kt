package com.nemislimus.tratometr.expenses.ui.fragment.recycler

import android.graphics.Canvas
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.tratometr.expenses.ui.fragment.ExpensesFragment
import com.nemislimus.tratometr.expenses.ui.fragment.adpter.ExpensesAdapter
import com.nemislimus.tratometr.expenses.ui.fragment.viewholder.ExpensesViewHolder

class ExpensesRecyclerView(
    val expensesFragment: ExpensesFragment,
    private val recycler: RecyclerView,
    private val adapter: ExpensesAdapter
) {

    fun setupRecyclerView() {
        recycler.adapter = adapter
        recycler.itemAnimator = DefaultItemAnimator().apply {
            removeDuration = 150
        }
        recycler.scheduleLayoutAnimation()
        recycler.invalidate()
    }

    fun createItemTouchHelper(): ItemTouchHelper {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            val density = expensesFragment.requireContext().resources.displayMetrics.density
            val maxShift = -112 * density                            // Величина максимального смещения при свайпе
            var isShift = false
            lateinit var c: Canvas
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun isLongPressDragEnabled(): Boolean = false
            override fun isItemViewSwipeEnabled(): Boolean = true
            // Сделать свайп грубее
            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 1.2f
            override fun getSwipeEscapeVelocity(defaultValue: Float): Float = 100 * defaultValue
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            // Премещает FOREGROUND, оставляя BACKGROUND на месте
            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, dX: Float,
                dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                this.c = c
                if (viewHolder is ExpensesViewHolder) { // Только для холдеров, содержащих сироку расхода
                    val holder = viewHolder
                    val foregroundView = holder.flForeground
                    val moveX = foregroundView.x
                    isShift = false
                    val distX: Float = if (isCurrentlyActive){
                        dX
                    } else {
                        if (moveX > maxShift) 0f else maxShift
                    }
                    getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, distX, 0.0f, actionState, isCurrentlyActive)
                    isShift = true
                }
            }

            // Перерисовывает ViewHolder после манипуляций
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                if (viewHolder is ExpensesViewHolder)
                    if (isShift) {
                        // Переводим фокус на background
                        val backgroundView = viewHolder.flBackground
                        if (!backgroundView.hasFocus()) backgroundView.requestFocus()
                    } else {
                        // Возвращаем foreground в исходное положение
                        expensesFragment.returnHolderToOriginalState(viewHolder)
                    }
            }
        }
        return ItemTouchHelper(callback)
    }
}