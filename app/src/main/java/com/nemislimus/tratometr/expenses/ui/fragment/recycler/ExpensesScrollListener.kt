package com.nemislimus.tratometr.expenses.ui.fragment.recycler

import androidx.recyclerview.widget.RecyclerView
import com.a2t.myapplication.main.ui.activity.recycler.model.ScrollState

class ExpensesScrollListener(private val listener: OnScrollStateChangedListener) :
    RecyclerView.OnScrollListener() {
    private var scrollState: ScrollState = ScrollState.STOPPED

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        scrollState = when {
            dy > 0 -> ScrollState.DOWN // Прокрутка вниз
            dy < 0 -> ScrollState.UP // Прокрутка вверх
            else -> ScrollState.STOPPED // Прокрутка остановлена
        }
        listener.onScrollStateChanged(scrollState)
    }
}