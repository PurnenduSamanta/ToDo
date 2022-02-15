package com.purnendu.todo.activity

import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purnendu.todo.*
import com.purnendu.todo.database.AppDatabase
import com.purnendu.todo.databinding.ActivityMainBinding
import com.purnendu.todo.swipe.SwipeGesture
import com.purnendu.todo.swipe.SwipeViewDecoration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var recyclerViewAdapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)


        setSupportActionBar(binding.toolbar)

        database = AppDatabase.getDataBase(this)


        recyclerViewAdapter = Adapter()

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
                    R.drawable.done
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
                val id = recyclerViewAdapter.getTaskItemId(viewHolder.bindingAdapterPosition)
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

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.recyclerView)


        binding.recyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }



        binding.fab.setOnClickListener {
            startActivity(Intent(this@MainActivity, TaskActivity::class.java))
        }

        addObserver()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView

        displaySearchResult(item, searchView)

        return super.onCreateOptionsMenu(menu)
    }


    private fun displaySearchResult(menuItem: MenuItem, searchView: SearchView) {
        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                addObserver()
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotBlank())
                    addObserver(true, newText)
                return true
            }

        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.history -> startActivity(Intent(this, HistoryActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addObserver(fromSearchView: Boolean = false, newText: String = "") {
        database.taskDao().getTask().observe(this) { outerIt ->

            if (outerIt != null) {
                if (!fromSearchView) {
                    //  recyclerViewAdapter.setData(outerIt.reversed())
                    recyclerViewAdapter.submitList(outerIt.reversed())
                    binding.noTask.apply {
                        visibility = if (outerIt.isEmpty())
                            View.VISIBLE
                        else
                            View.INVISIBLE
                    }
                } else {
                    recyclerViewAdapter.submitList(outerIt.filter {
                        it.title.contains(newText, true)
                    })
                }

            }
        }
    }
}