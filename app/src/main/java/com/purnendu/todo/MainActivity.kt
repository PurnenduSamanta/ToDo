package com.purnendu.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    lateinit var fab: FloatingActionButton
    lateinit var noTask:ImageView
    private lateinit var database: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter
    private var list = arrayListOf<TaskModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        fab = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.taskRv)
        noTask=findViewById(R.id.noTask)

        setSupportActionBar(toolbar)

        database = AppDatabase.getDataBase(this)


        adapter = Adapter(list)

        val swipeGesture = object : SwipeGesture() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id=adapter.getItemId(viewHolder.absoluteAdapterPosition)
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            database.taskDao().deletedTask(id)
                        }
                    }
                    ItemTouchHelper.LEFT -> {
                       CoroutineScope(Dispatchers.Default).launch {
                            database.taskDao().finishedTask(id)
                        }
                    }
                }
            }
        }

        val touchHelper=ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recyclerView)




        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        fab.setOnClickListener {

            startActivity(Intent(this, TaskActivity::class.java))
        }

       addObserver()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.history -> startActivity(Intent(this, HistoryActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addObserver()
    {
        database.taskDao().getTask().observe(this) {

           if(it!=null)
           {
               adapter.setData(it)
               if(it.isEmpty())
               noTask.visibility= View.VISIBLE
               else
                   noTask.visibility= View.INVISIBLE
           }



        }
    }
}