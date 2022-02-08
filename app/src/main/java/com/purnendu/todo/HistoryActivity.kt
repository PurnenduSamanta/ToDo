package com.purnendu.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.purnendu.todo.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var database: AppDatabase
    private lateinit var recyclerViewAdapter: Adapter
    private var list = arrayListOf<TaskModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)

        setSupportActionBar(binding.historyToolbar)

        database = AppDatabase.getDataBase(this)

        recyclerViewAdapter = Adapter(list)
        binding.historyRecyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }

        database.taskDao().getFinishedTask().observe(this)
        {

            if (it != null) {
                if (it.isNotEmpty())
                    recyclerViewAdapter.setData(it)
                else
                    Toast.makeText(this, "No Task found", Toast.LENGTH_SHORT).show()

            }

        }


    }
}