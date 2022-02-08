package com.purnendu.todo

import android.content.Intent
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
import com.purnendu.todo.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var recyclerViewAdapter: Adapter
    private var list = arrayListOf<TaskModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)


        setSupportActionBar(binding.toolbar)

        database = AppDatabase.getDataBase(this)


        recyclerViewAdapter = Adapter(list)

        val swipeGesture = object : SwipeGesture() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id = recyclerViewAdapter.getItemId(viewHolder.absoluteAdapterPosition)
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
            startActivity(Intent(this@MainActivity,TaskActivity::class.java))
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
                    recyclerViewAdapter.setData(outerIt)
                    binding.noTask.apply {
                        visibility = if (outerIt.isEmpty())
                            View.VISIBLE
                        else
                            View.INVISIBLE
                    }
                } else {
                    recyclerViewAdapter.setData(outerIt.filter {
                        it.title.contains(newText, true)
                    })
                }

            }
        }
    }
}