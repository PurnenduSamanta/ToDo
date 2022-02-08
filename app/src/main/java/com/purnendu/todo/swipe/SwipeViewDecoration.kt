package com.purnendu.todo.swipe

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import com.purnendu.todo.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


object SwipeViewDecoration {
    fun decorator(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX:Float, dY:Float, actionState:Int, isCurrentlyActive:Boolean, iconId:Int) {
        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeLeftActionIcon(iconId)
            .addSwipeRightActionIcon(R.drawable.delete)
            .addSwipeRightBackgroundColor(android.graphics.Color.RED)
            .addSwipeLeftBackgroundColor(android.graphics.Color.GREEN)
            .create()
            .decorate()
    }
}

