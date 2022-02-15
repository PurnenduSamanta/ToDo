package com.purnendu.todo.activity

import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purnendu.todo.*
import com.purnendu.todo.database.AppDatabase
import com.purnendu.todo.databinding.ActivityHistoryBinding
import com.purnendu.todo.swipe.SwipeGesture
import com.purnendu.todo.swipe.SwipeViewDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var database: AppDatabase
    private lateinit var recyclerViewAdapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)

        setSupportActionBar(binding.historyToolbar)

        database = AppDatabase.getDataBase(this)

        recyclerViewAdapter = Adapter()
        binding.historyRecyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }


        val swipeGesture = object : SwipeGesture() {


            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                SwipeViewDecoration.decorator(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive,
                    R.drawable.undo
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id = recyclerViewAdapter.getTaskItemId(viewHolder.absoluteAdapterPosition)
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            database.taskDao().deletedTask(id)
                        }
                    }
                    ItemTouchHelper.LEFT -> {
                        CoroutineScope(Dispatchers.Default).launch {
                            database.taskDao().unFinishedTask(id)
                        }
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.historyRecyclerView)

        database.taskDao().getFinishedTask().observe(this)
        {

            if (it != null) {
                recyclerViewAdapter.submitList(it.reversed())
                if (it.isEmpty())
                    Toast.makeText(this, "No Task found", Toast.LENGTH_SHORT).show()

            }

        }


    }
}